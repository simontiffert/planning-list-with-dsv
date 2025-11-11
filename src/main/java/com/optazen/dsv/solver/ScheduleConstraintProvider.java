package com.optazen.dsv.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import com.optazen.dsv.domain.SubTask;
import org.jspecify.annotations.NonNull;

import java.time.Duration;

public class ScheduleConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint @NonNull [] defineConstraints(@NonNull ConstraintFactory constraintFactory) {
        return new Constraint[] {
                penalizeInconsistent(constraintFactory),
                minimizeMakespan(constraintFactory)
        };
    }

    protected Constraint penalizeInconsistent(ConstraintFactory factory) {
        return factory.forEachUnfiltered(SubTask.class)
                .filter(SubTask::isInconsistent)
                .penalize(HardSoftLongScore.ONE_HARD)
                .asConstraint("Inconsistent");
    }

    protected Constraint minimizeMakespan(ConstraintFactory factory) {
        return factory.forEach(SubTask.class)
                .filter(subTask -> subTask.getEndDateTime() != null)
                .filter(subTask -> subTask.getNext() == null)
                .penalizeLong(HardSoftLongScore.ONE_SOFT, subTask -> {
                    long minutes = Duration.between(subTask.getResource().getResourceStartDateTime(), subTask.getEndDateTime()).toMinutes();
                    return minutes * minutes;
                })
                .asConstraint("Minimize make span");
    }
}
