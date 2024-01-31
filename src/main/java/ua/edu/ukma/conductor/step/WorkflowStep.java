package ua.edu.ukma.conductor.step;

import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.workflow.Workflow;
import ua.edu.ukma.conductor.task.Result;

/**
 * Single unit in the {@link Workflow} structure that encapsulates {@link ua.edu.ukma.conductor.task.Task}
 * and applies its result by mutating the state of the workflow.
 *
 * @param <S> state type
 */
public abstract class WorkflowStep<S extends WorkflowState<S>> implements WorkflowStepOrBuilder<S> {
    public static final String DEFAULT_NAME = "Unnamed";

    private final String name;

    protected WorkflowStep(String name) {
        this.name = name;
    }

    protected WorkflowStep() {
        this.name = DEFAULT_NAME;
    }

    public abstract Result<S> execute(S initialState);

    @Override
    public WorkflowStep<S> toStep() {
        return this;
    }

    public String name() {
        return name;
    }
}
