package com.optazen.dsv.domain;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@PlanningSolution
public class Schedule {
    @PlanningEntityCollectionProperty
    @ValueRangeProvider
    private List<Resource> resources = new ArrayList();

    @PlanningEntityCollectionProperty
    @ValueRangeProvider
    private List<SubTask> subTasks = new ArrayList();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @PlanningScore
    private HardSoftLongScore score;

    public Schedule() {
    }

    public Schedule(List<Resource> resources, List<SubTask> subTasks) {
        this.resources = resources;
        this.subTasks = subTasks;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public HardSoftLongScore getScore() {
        return score;
    }

    public void setScore(HardSoftLongScore score) {
        this.score = score;
    }
}
