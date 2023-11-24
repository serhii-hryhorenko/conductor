package ua.edu.ukma.conductor.state;

import java.util.function.Consumer;

public abstract class WorkflowState<S> {
    public abstract S copy();

    public final S reduce(Consumer<S> stateMutator) {
        S copy = copy();
        stateMutator.accept(copy);

        return copy;
    }
}
