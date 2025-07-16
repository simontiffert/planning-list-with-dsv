package com.optazen.dsv.rest;

import ai.timefold.solver.core.api.solver.SolverJob;
import ai.timefold.solver.core.api.solver.SolverManager;
import com.optazen.dsv.domain.Resource;
import com.optazen.dsv.domain.Schedule;
import com.optazen.dsv.domain.SubTask;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Path("/api")
public class ApiResource {
    protected static final Logger logger = LoggerFactory.getLogger(ApiResource.class);
    @Inject
    SolverManager<Schedule, String> solverManager;

    @POST()
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/solve")
    public Schedule solve(Schedule schedule) {
        SolverJob<Schedule, String> solverJob = solverManager.solveBuilder()
                .withProblemId(String.valueOf(UUID.randomUUID()))
                .withProblem(schedule)
                .withBestSolutionConsumer(solution -> logger.debug("New best solution ({}).", solution.getScore()))
                .withExceptionHandler((jobId_, exception) -> logger.error("Failed solving jobId ({}).", jobId_, exception))
                .run();

        try {
            return solverJob.getFinalBestSolution();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/demoData")
    public Schedule demoData() {
        LocalDateTime planStart = LocalDateTime.of(2025, 7, 16, 14, 0);
        List<Resource> resources = new ArrayList<>();
        int resourceCounter = 1;
        resources.add(new Resource("R" + resourceCounter++, planStart));
        resources.add(new Resource("R" + resourceCounter++, planStart));

        List<SubTask> subTasks = new ArrayList<>();
        int subTaskCounter = 1;
        for(int i = 0; i < 5; i++) {
            SubTask task1 = new SubTask("S" + subTaskCounter++, "" + i, Duration.ofMinutes(10), List.of());
            SubTask task2 = new SubTask("S" + subTaskCounter++, "" + i, Duration.ofMinutes(10), List.of());
            SubTask task3 = new SubTask("S" + subTaskCounter++, "" + i, Duration.ofMinutes(10), List.of(task1, task2));
            subTasks.add(task1);
            subTasks.add(task2);
            subTasks.add(task3);
        }

        return new Schedule(resources, subTasks);
    }
}
