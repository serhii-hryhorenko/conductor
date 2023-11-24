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
public abstract class Step<S extends WorkflowState<S>> extends StepOrBuilder<S> {
    public abstract Result<S> execute(S initialState);

    @Override
    protected Step<S> toStep() {
        return this;
    }
}
