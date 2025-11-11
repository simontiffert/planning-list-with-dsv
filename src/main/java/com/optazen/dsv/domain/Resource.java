package com.optazen.dsv.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningListVariable;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@PlanningEntity
public class Resource {
    @PlanningId
    private String id;

    @PlanningListVariable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<SubTask> subTasks = new ArrayList<>();

    private LocalDateTime resourceStartDateTime;

    public Resource() {
    }

    public Resource(String id, LocalDateTime resourceStartDateTime) {
        this.id = id;
        this.resourceStartDateTime = resourceStartDateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public LocalDateTime getResourceStartDateTime() {
        return resourceStartDateTime;
    }

    public void setResourceStartDateTime(LocalDateTime resourceStartDateTime) {
        this.resourceStartDateTime = resourceStartDateTime;
    }

    @Override
    public String toString() {
        return id;
    }
}
