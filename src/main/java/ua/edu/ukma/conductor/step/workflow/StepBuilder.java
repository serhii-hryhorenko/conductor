package ua.edu.ukma.conductor.step.workflow;

import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.WorkflowStep;
import ua.edu.ukma.conductor.step.WorkflowStepOrBuilder;
import ua.edu.ukma.conductor.task.Task;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class StepBuilder<S extends WorkflowState<S>, P, V> extends WorkflowStepOrBuilder<S> {
    private final Task<P, V> task;
    private Function<S, P> stateMapper;
    private BiConsumer<S, V> stateReducer;
    private Consumer<V> successHandler;
    private Consumer<Throwable> errorHandler;

    StepBuilder(Task<P, V> task) {
        this.task = task;
    }

    public StepBuilder<S, P, V> thatAccepts(Function<S, P> stateMapper) {
        this.stateMapper = stateMapper;
        return this;
    }

    public StepBuilder<S, P, V> reducesState(BiConsumer<S, V> stateReducer) {
        this.stateReducer = stateReducer;
        return this;
    }

    public StepBuilder<S, P, V> onSuccess(Consumer<V> successHandler) {
        this.successHandler = successHandler;
        return this;
    }

    public StepBuilder<S, P, V> onFail(Consumer<Throwable> failHandler) {
        this.errorHandler = failHandler;
        return this;
    }

    public Step<S, P, V> build() {
        Objects.requireNonNull(stateMapper);
        return new Step<>(task, stateMapper, stateReducer, successHandler, errorHandler);
    }

    @Override
    protected WorkflowStep<S> toStep() {
        return build();
    }
}
