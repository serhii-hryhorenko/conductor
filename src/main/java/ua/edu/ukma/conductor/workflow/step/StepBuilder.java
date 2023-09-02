package ua.edu.ukma.conductor.workflow.step;

import ua.edu.ukma.conductor.task.PayloadType;
import ua.edu.ukma.conductor.task.ResultType;
import ua.edu.ukma.conductor.task.Task;
import ua.edu.ukma.conductor.workflow.WorkflowState;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class StepBuilder<V extends ResultType, S extends WorkflowState<S>, P extends PayloadType> {
    private final Task<V, P> task;
    private Function<S, P> stateMapper;
    private BiConsumer<S, V> stateReducer;
    private Consumer<V> successHandler;
    private Consumer<Throwable> errorHandler;
    private Consumer<Void> fallbackHandler;


    StepBuilder(Task<V, P> task) {
        this.task = task;
    }

    public StepBuilder<V, S, P> thatAccepts(Function<S, P> stateMapper) {
        this.stateMapper = stateMapper;
        return this;
    }

    public StepBuilder<V, S, P> reducingState(BiConsumer<S, V> stateReducer) {
        this.stateReducer = stateReducer;
        return this;
    }

    public StepBuilder<V, S, P> withSuccessHandler(Consumer<V> successHandler) {
        this.successHandler = successHandler;
        return this;
    }

    public StepBuilder<V, S, P> withErrorHandler(Consumer<Throwable> errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    public StepBuilder<V, S, P> withFallbackHandler(Consumer<Void> fallbackHandler) {
        this.fallbackHandler = fallbackHandler;
        return this;
    }

    public Step<V, S, P> create() {
        Objects.requireNonNull(stateMapper);
        return new Step<>(task, stateMapper, stateReducer, successHandler, errorHandler, fallbackHandler);
    }
}
