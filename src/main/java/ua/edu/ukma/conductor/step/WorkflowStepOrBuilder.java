package ua.edu.ukma.conductor.step;

import ua.edu.ukma.conductor.state.WorkflowState;

public interface WorkflowStepOrBuilder<S extends WorkflowState<S>> {
    WorkflowStep<S> toStep();
}
