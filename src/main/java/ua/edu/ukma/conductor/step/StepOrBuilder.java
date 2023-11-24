package ua.edu.ukma.conductor.step;

import ua.edu.ukma.conductor.state.WorkflowState;

public abstract class StepOrBuilder<S extends WorkflowState<S>> {
    protected abstract Step<S> toStep();
}
