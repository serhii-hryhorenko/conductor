package ua.edu.ukma.conductor.step;

import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.workflow.Workflow;
import ua.edu.ukma.conductor.task.Result;

import java.util.UUID;

/**
 * Single unit in the {@link Workflow} structure that encapsulates {@link ua.edu.ukma.conductor.task.Task}
 * and applies its result by mutating the state of the workflow.
 *
 * @param <S> state type
 */
public abstract class WorkflowStep<S extends WorkflowState<S>> extends WorkflowStepOrBuilder<S> {
    public static final String DEFAULT_NAME = "Unnamed";

    private final UUID id = UUID.randomUUID();
    private final String name;

    protected WorkflowStep(String name) {
        this.name = name;
    }

    protected WorkflowStep() {
        this.name = DEFAULT_NAME;
    }

    public abstract Result<S> execute(S initialState);

    @Override
    protected WorkflowStep<S> toStep() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkflowStep)) return false;

        WorkflowStep<?> that = (WorkflowStep<?>) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String name() {
        return name;
    }

    public UUID id() {
        return id;
    }
}
