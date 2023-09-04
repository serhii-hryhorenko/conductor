package ua.edu.ukma.conductor.workflow;

import ua.edu.ukma.conductor.task.Result;

public abstract class Step<S extends WorkflowState<S>> extends StepOrBuilder<S> {
    public abstract Result<S> execute(S initialState);

    @Override
    protected Step<S> step() {
        return this;
    }
}
