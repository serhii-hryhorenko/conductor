package ua.edu.ukma.conductor.step;

import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.workflow.Workflow;
import ua.edu.ukma.conductor.task.Result;
import ua.edu.ukma.conductor.task.Task;

import java.util.Optional;

/**
 * Single unit in the {@link Workflow} structure that does job and applies result to the {@link WorkflowState}.
 *
 * @param <S> state type
 */
public abstract class WorkflowStep<S extends WorkflowState<S>> implements WorkflowStepOrBuilder<S>, Task<S, S> {
    public static final String DEFAULT_NAME = "Unnamed";

    private final String name;

    protected WorkflowStep(String name) {
        this.name = Optional.ofNullable(name).orElse(DEFAULT_NAME);
    }

    protected WorkflowStep() {
        this.name = DEFAULT_NAME;
    }

    @Override
    public WorkflowStep<S> toStep() {
        return this;
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
