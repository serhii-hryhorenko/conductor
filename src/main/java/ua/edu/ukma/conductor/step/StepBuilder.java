package ua.edu.ukma.conductor.step;

import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.workflow.WorkflowBuilder;

public abstract class StepBuilder<B extends WorkflowBuilder<B, S>,
        W extends Step<S>,
        S extends WorkflowState<S>> extends StepOrBuilder<S> {

    public abstract B addStep(Step<S> step);

    public B addStep(StepOrBuilder<S> orBuilder) {
        return addStep(orBuilder.toStep());
    }

    public abstract W build();

    @Override
    protected Step<S> toStep() {
        return build();
    }
}
