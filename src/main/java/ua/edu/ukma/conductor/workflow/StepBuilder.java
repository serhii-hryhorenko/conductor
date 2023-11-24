package ua.edu.ukma.conductor.workflow;

public abstract class StepBuilder<B extends WorkflowBuilder<B, S>,
        W extends Step<S>,
        S extends WorkflowState<S>> extends StepOrBuilder<S> {

    public abstract B addStep(Step<S> step);

    public abstract W build();

    @Override
    protected Step<S> step() {
        return build();
    }
}
