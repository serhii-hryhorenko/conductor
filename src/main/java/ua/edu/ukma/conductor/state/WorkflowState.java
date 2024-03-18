package ua.edu.ukma.conductor.state;

import java.util.function.Consumer;

public interface WorkflowState<S> {
    /**
     * Returns a copy of the state.
     */
    S copy();


    /**
     * Returns a new state that is the result of applying the reducer to the current state.
     */
    static <S extends WorkflowState<S>> S reduce(S state, Consumer<S> reducer) {
        S copy = state.copy();
        reducer.accept(copy);

        return copy;
    }
}
