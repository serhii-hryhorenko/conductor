package ua.edu.ukma.conductor.workflow;

public abstract class StepOrBuilder<S extends WorkflowState<S>> {
    protected abstract Step<S> step();
}
