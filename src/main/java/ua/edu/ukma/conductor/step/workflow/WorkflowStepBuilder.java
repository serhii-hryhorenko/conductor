package ua.edu.ukma.conductor.step.workflow;

import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.WorkflowStep;
import ua.edu.ukma.conductor.step.WorkflowStepOrBuilder;
import ua.edu.ukma.conductor.task.Task;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class WorkflowStepBuilder<S extends WorkflowState<S>, P, V> implements WorkflowStepOrBuilder<S> {
    private final Task<P, V> task;
    private String name;
    private Function<S, P> stateMapper;
    private BiConsumer<S, V> stateReducer;
    private Consumer<V> successHandler;
    private Consumer<Throwable> errorHandler;

    WorkflowStepBuilder(Task<P, V> task) {
        this.task = task;
    }

    public WorkflowStepBuilder<S, P, V> named(String name) {
        this.name = name;
        return this;
    }

    public WorkflowStepBuilder<S, P, V> thatAccepts(Function<S, P> stateMapper) {
        this.stateMapper = stateMapper;
        return this;
    }

    public WorkflowStepBuilder<S, P, V> reducesState(BiConsumer<S, V> stateReducer) {
        this.stateReducer = stateReducer;
        return this;
    }

    public WorkflowStepBuilder<S, P, V> onSuccess(Consumer<V> successHandler) {
        this.successHandler = successHandler;
        return this;
    }

    public WorkflowStepBuilder<S, P, V> onFail(Consumer<Throwable> failHandler) {
        this.errorHandler = failHandler;
        return this;
    }

    public Step<S, P, V> build() {
        return new Step<>(
                requireNonNull(task),
                name,
                requireNonNull(stateMapper),
                requireNonNull(stateReducer),
                successHandler,
                errorHandler
        );
    }

    @Override
    public WorkflowStep<S> toStep() {
        return build();
    }
}
