package ua.edu.ukma.conductor.step;

import ua.edu.ukma.conductor.state.WorkflowState;

public abstract class WorkflowStepOrBuilder<S extends WorkflowState<S>> {
    protected abstract WorkflowStep<S> toStep();
}
