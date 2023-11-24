package ua.edu.ukma.conductor.step;

import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.workflow.WorkflowBuilder;

public abstract class StepBuilder<B extends WorkflowBuilder<B, S>,
        W extends WorkflowStep<S>,
        S extends WorkflowState<S>> extends WorkflowStepOrBuilder<S> {

    public abstract B addStep(WorkflowStep<S> step);

    public B addStep(WorkflowStepOrBuilder<S> orBuilder) {
        return addStep(orBuilder.toStep());
    }

    public abstract W build();

    @Override
    protected WorkflowStep<S> toStep() {
        return build();
    }
}
