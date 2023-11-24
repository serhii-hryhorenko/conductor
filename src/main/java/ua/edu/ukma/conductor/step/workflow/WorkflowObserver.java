package ua.edu.ukma.conductor.step.workflow;

import ua.edu.ukma.conductor.state.WorkflowState;

@FunctionalInterface
public interface WorkflowObserver<S extends WorkflowState<S>> {
    void observe(S workflowState);
}
