package ua.edu.ukma.conductor.workflow;

import java.util.function.Consumer;

public abstract class WorkflowState<I> {
    protected abstract I copy();

    public I reduce(Consumer<I> stateReducer) {
        I copy = copy();
        stateReducer.accept(copy);

        return copy;
    }
}
