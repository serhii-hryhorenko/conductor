package ua.edu.ukma.conductor.workflow.step;

import ua.edu.ukma.conductor.task.Task;
import ua.edu.ukma.conductor.workflow.WorkflowState;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class StepBuilder<S extends WorkflowState<S>, V, P> {
    private final Task<V, P> task;
    private Function<S, P> stateMapper;
    private BiConsumer<S, V> stateReducer;
    private Consumer<V> successHandler;
    private Consumer<Throwable> errorHandler;
    private Consumer<Void> fallbackHandler;


    StepBuilder(Task<V, P> task) {
        this.task = task;
    }

    public StepBuilder<S, V, P> thatAccepts(Function<S, P> stateMapper) {
        this.stateMapper = stateMapper;
        return this;
    }

    public StepBuilder<S, V, P> reducingState(BiConsumer<S, V> stateReducer) {
        this.stateReducer = stateReducer;
        return this;
    }

    public StepBuilder<S, V, P> withSuccessHandler(Consumer<V> successHandler) {
        this.successHandler = successHandler;
        return this;
    }

    public StepBuilder<S, V, P> withErrorHandler(Consumer<Throwable> errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    public StepBuilder<S, V, P> withFallbackHandler(Consumer<Void> fallbackHandler) {
        this.fallbackHandler = fallbackHandler;
        return this;
    }

    public Step<S, P, V> create() {
        Objects.requireNonNull(stateMapper);
        return new Step<>(task, stateMapper, stateReducer, successHandler, errorHandler, fallbackHandler);
    }
}
