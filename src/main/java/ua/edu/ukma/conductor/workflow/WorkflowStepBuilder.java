package ua.edu.ukma.conductor.workflow;

import ua.edu.ukma.conductor.task.Task;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class WorkflowStepBuilder<S extends WorkflowState<S>, P, V> extends StepOrBuilder<S> {
    private final Task<P, V> task;
    private Function<S, P> stateMapper;
    private BiConsumer<S, V> stateReducer;
    private Consumer<V> successHandler;
    private Consumer<Throwable> errorHandler;
    private Consumer<Void> fallbackHandler;

    WorkflowStepBuilder(Task<P, V> task) {
        this.task = task;
    }

    public WorkflowStepBuilder<S, P, V> thatAccepts(Function<S, P> stateMapper) {
        this.stateMapper = stateMapper;
        return this;
    }

    public WorkflowStepBuilder<S, P, V> reducingState(BiConsumer<S, V> stateReducer) {
        this.stateReducer = stateReducer;
        return this;
    }

    public WorkflowStepBuilder<S, P, V> withSuccessHandler(Consumer<V> successHandler) {
        this.successHandler = successHandler;
        return this;
    }

    public WorkflowStepBuilder<S, P, V> withErrorHandler(Consumer<Throwable> errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    public WorkflowStepBuilder<S, P, V> withFallbackHandler(Consumer<Void> fallbackHandler) {
        this.fallbackHandler = fallbackHandler;
        return this;
    }

    public WorkflowStep<S, P, V> build() {
        Objects.requireNonNull(stateMapper);
        return new WorkflowStep<>(task, stateMapper, stateReducer, successHandler, errorHandler, fallbackHandler);
    }

    @Override
    protected Step<S> step() {
        return build();
    }
}
