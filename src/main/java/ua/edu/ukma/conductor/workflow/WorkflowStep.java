package ua.edu.ukma.conductor.workflow;

import ua.edu.ukma.conductor.task.Result;

public interface WorkflowStep<S extends WorkflowState<S>> {
    Result<S> execute(S initialState);
}
