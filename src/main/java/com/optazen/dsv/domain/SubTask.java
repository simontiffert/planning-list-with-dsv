package com.optazen.dsv.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.*;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@PlanningEntity
public class SubTask {
    protected static final Logger logger = LoggerFactory.getLogger(SubTask.class);

    @PlanningId
    private String id;

    private Duration duration;

    private String task;

    @InverseRelationShadowVariable(sourceVariableName = "subTasks")
    @JsonIdentityReference(alwaysAsId = true)
    private Resource resource;
    @JsonIgnore
    @PreviousElementShadowVariable(sourceVariableName = "subTasks")
    // The previous process step execution on the asset
    private SubTask previous;
    @JsonIgnore
    @NextElementShadowVariable(sourceVariableName = "subTasks")
    // The next process step execution on the asset
    private SubTask next;

    @JsonIgnore
    @ShadowVariablesInconsistent
    private boolean inconsistent;

    @JsonIdentityReference(alwaysAsId = true)
    private List<SubTask> predecessors = List.of();

    // Automatic delay start until last - https://docs.timefold.ai/timefold-solver/latest/design-patterns/design-patterns#chainedThroughTimeAutomaticDelayUntilLast
    @JsonIdentityReference(alwaysAsId = true)
    private List<SubTask> concurrentSubTasks = List.of();

    @ShadowVariable(supplierName = "updateReadyDateTime")
    private LocalDateTime readyDateTime;
    @ShadowVariable(supplierName = "updateStartDateTime")
    private LocalDateTime startDateTime;
    @ShadowVariable(supplierName = "updateEndDateTime")
    private LocalDateTime endDateTime;

    public SubTask() {
    }

    public SubTask(String id, String task, Duration duration, List<SubTask> predecessors) {
        this.id = id;
        this.task = task;
        this.duration = duration;
        this.predecessors = predecessors;
    }

    @ShadowSources({
            "resource",
            "previous.endDateTime",
            "predecessors[].endDateTime"})
    protected LocalDateTime updateReadyDateTime() {
        // When there is not yet a resource assigned as PlanningVariable => null
        if (resource == null) {
            return null;
        }

        // If there is a previous sub-task assigned on the resource - use the end time (could be null)
        LocalDateTime tmpReadyDateTime = previous == null ? resource.getResourceStartDateTime() : previous.getEndDateTime();

        // inconsistent previous variable
        if (tmpReadyDateTime == null) {
            return null;
        }

        // loop through the predecessors and check if a delay is needed when a predecessor is finished later as the previous task
        for(SubTask predecessor : predecessors) {
            if(predecessor.endDateTime != null && predecessor.endDateTime.isAfter(tmpReadyDateTime)) {
                tmpReadyDateTime = predecessor.endDateTime;
            }
        }
        return tmpReadyDateTime;
    }

    @ShadowSources(value = {
            "readyDateTime",
            "concurrentSubTasks[].readyDateTime"
    }
//            , alignmentKey = "concurrentSubTasks"
    )
    protected LocalDateTime updateStartDateTime() {
        if(readyDateTime == null) {
            return null;
        }
       LocalDateTime tmpStartDateTime = readyDateTime;
       for(SubTask subTask : concurrentSubTasks) {
           if(subTask.readyDateTime != null && subTask.readyDateTime.isAfter(tmpStartDateTime)) {
               tmpStartDateTime = subTask.readyDateTime;
           }
       }
       return tmpStartDateTime;
    }

    @ShadowSources({"startDateTime"})
    protected LocalDateTime updateEndDateTime() {
        return startDateTime == null ? null : startDateTime.plus(getDuration());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public SubTask getPrevious() {
        return previous;
    }

    public void setPrevious(SubTask previous) {
        this.previous = previous;
    }

    public SubTask getNext() {
        return next;
    }

    public void setNext(SubTask next) {
        this.next = next;
    }

    public List<SubTask> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(List<SubTask> predecessors) {
        this.predecessors = predecessors;
    }

    public List<SubTask> getConcurrentSubTasks() {
        return concurrentSubTasks;
    }

    public void setConcurrentSubTasks(List<SubTask> concurrentSubTasks) {
        this.concurrentSubTasks = concurrentSubTasks;
    }

    public LocalDateTime getReadyDateTime() {
        return readyDateTime;
    }

    public void setReadyDateTime(LocalDateTime readyDateTime) {
        this.readyDateTime = readyDateTime;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public boolean isInconsistent() {
        return inconsistent;
    }

    public void setInconsistent(boolean inconsistent) {
        this.inconsistent = inconsistent;
    }

    @Override
    public String toString() {
        return id + " " + resource + (previous == null ? "" : " prev: " + previous.getId());
    }
}
