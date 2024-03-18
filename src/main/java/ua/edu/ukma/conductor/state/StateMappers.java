package ua.edu.ukma.conductor.state;

import java.util.function.Function;

public class StateMappers {
    private StateMappers() {
    }

    /**
     * A function that returns a copy of the input state.
     * @param <S> The type of the state.
     */
    public static <S extends WorkflowState<S>> Function<S, S> workflowState() {
        return WorkflowState::copy;
    }

    /**
     * A function that returns payload for {@link Void} type.
     * @param <S> The type of the state.
     */
    public static <S extends WorkflowState<S>> Function<S, Void> noPayload() {
        return state -> null;
    }
}
