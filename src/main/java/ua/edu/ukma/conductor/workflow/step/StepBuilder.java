package ua.edu.ukma.conductor.workflow.step;

import ua.edu.ukma.conductor.task.Task;
import ua.edu.ukma.conductor.workflow.WorkflowState;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class StepBuilder<T, S extends WorkflowState<S>, P> {
    private final Task<T, P> task;
    private Function<S, P> stateMapper;
    private BiConsumer<S, T> stateReducer;
    private Consumer<T> successHandler;
    private Consumer<Throwable> errorHandler;
    private Consumer<Void> fallbackHandler;


    StepBuilder(Task<T, P> task) {
        this.task = task;
    }

    public StepBuilder<T, S, P> thatAccepts(Function<S, P> stateMapper) {
        this.stateMapper = stateMapper;
        return this;
    }

    public StepBuilder<T, S, P> reducingState(BiConsumer<S, T> stateReducer) {
        this.stateReducer = stateReducer;
        return this;
    }

    public StepBuilder<T, S, P> withSuccessHandler(Consumer<T> successHandler) {
        this.successHandler = successHandler;
        return this;
    }

    public StepBuilder<T, S, P> withErrorHandler(Consumer<Throwable> errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    public StepBuilder<T, S, P> withFallbackHandler(Consumer<Void> fallbackHandler) {
        this.fallbackHandler = fallbackHandler;
        return this;
    }

    public Step<T, S, P> create() {
        Objects.requireNonNull(stateMapper);
        return new Step<>(task, stateMapper, stateReducer, successHandler, errorHandler, fallbackHandler);
    }
}
