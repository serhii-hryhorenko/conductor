package ua.edu.ukma.conductor.workflow;

import java.util.function.Consumer;

public abstract class WorkflowState<S> {
    protected abstract S copy();

    public S reduce(Consumer<S> stateReducer) {
        S copy = copy();
        stateReducer.accept(copy);

        return copy;
    }
}
