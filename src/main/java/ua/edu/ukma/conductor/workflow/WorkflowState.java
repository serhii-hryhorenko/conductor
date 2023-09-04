package ua.edu.ukma.conductor.workflow;

import java.util.function.Consumer;

public abstract class WorkflowState<S> {
    protected abstract S copy();

    public S mutate(Consumer<S> stateMutator) {
        S copy = copy();
        stateMutator.accept(copy);

        return copy;
    }
}
