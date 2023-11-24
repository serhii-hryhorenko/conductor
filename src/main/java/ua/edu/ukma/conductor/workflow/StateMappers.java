package ua.edu.ukma.conductor.workflow;

import java.util.function.Function;

public class StateMappers {
    private StateMappers() {
    }

    public static <S extends WorkflowState<S>> Function<S, S> workflowState() {
        return WorkflowState::copy;
    }

    public static <S extends WorkflowState<S>> Function<S, Void> noPayload() {
        return state -> null;
    }
}
