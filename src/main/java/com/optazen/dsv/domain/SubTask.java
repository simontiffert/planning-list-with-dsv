package com.optazen.dsv.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.InverseRelationShadowVariable;
import ai.timefold.solver.core.api.domain.variable.NextElementShadowVariable;
import ai.timefold.solver.core.api.domain.variable.PreviousElementShadowVariable;
import ai.timefold.solver.core.api.domain.variable.ShadowVariable;
import ai.timefold.solver.core.preview.api.domain.variable.declarative.ShadowSources;
import com.fasterxml.jackson.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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

    @JsonIdentityReference(alwaysAsId = true)
    private List<SubTask> predecessors;

    private SubTask unused;

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

    @ShadowSources({"resource", "previous.endDateTime", "predecessors[].endDateTime", "unused.endDateTime"})
//    @ShadowSources({"resource", "previous.endDateTime", "predecessors[].endDateTime"})
    protected LocalDateTime updateStartDateTime() {
        if (resource == null) {
            logger.trace("UpdateStartDateTime for {} on {} with result {} vs {}", getId(), resource, null,  startDateTime);
            return null;
        }

        LocalDateTime previousEnd = previous == null ? resource.getResourceStartDateTime() : previous.getEndDateTime();
        LocalDateTime predecessorEnd = this.getPredecessors() == null ? null : this.getPredecessors().stream()
                .map(SubTask::getEndDateTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime startDateTime = predecessorEnd != null
                && (previousEnd == null || predecessorEnd.isAfter(previousEnd))
                ? predecessorEnd
                : previousEnd;

        logger.trace("UpdateStartDateTime for {} on {} with previousEnd {} and predecessorEnd {} with result {} vs {}", id, resource, previousEnd, predecessorEnd, startDateTime, this.startDateTime);
        return startDateTime;
    }

    @ShadowSources({"startDateTime"})
    protected LocalDateTime updateEndDateTime() {
        LocalDateTime endDateTime = startDateTime == null ? null : startDateTime.plus(getDuration());
        logger.trace("UpdateEndDateTime for {} with startDateTime {} with result {} vs {}", id, startDateTime, endDateTime, this.endDateTime);
        return endDateTime;
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

    public SubTask getUnused() {
        return unused;
    }

    public void setUnused(SubTask unused) {
        this.unused = unused;
    }

    public List<SubTask> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(List<SubTask> predecessors) {
        this.predecessors = predecessors;
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
}
