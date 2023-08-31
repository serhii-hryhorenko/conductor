package ua.edu.ukma.conductor.workflow;

@FunctionalInterface
public interface WorkflowObserver<S extends WorkflowState<S>> {
    void observe(S workflowState);
}
