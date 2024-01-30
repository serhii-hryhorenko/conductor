package ua.edu.ukma.conductor.step.workflow;

import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.task.Result;
import ua.edu.ukma.conductor.task.Task;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class Step<S extends WorkflowState<S>, P, V> extends ua.edu.ukma.conductor.step.WorkflowStep<S> {
    private final UUID id = UUID.randomUUID();
    private final Task<P, V> task;
    private final String name;
    private final Function<S, P> stateProjector;

    private final BiConsumer<S, V> stateReducer;
    private final Consumer<V> successHandler;
    private final Consumer<Throwable> errorHandler;

    protected Step(Task<P, V> task,
                   String name,
                   Function<S, P> stateProjector,
                   BiConsumer<S, V> stateReducer,
                   Consumer<V> successHandler,
                   Consumer<Throwable> errorHandler) {
        this.task = task;
        this.name = name;
        this.stateProjector = stateProjector;
        this.stateReducer = stateReducer;
        this.successHandler = successHandler;
        this.errorHandler = errorHandler;
    }

    public static <S extends WorkflowState<S>, P, V>
    WorkflowStepBuilder<S, P, V> forTask(Task<P, V> task) {
        return new WorkflowStepBuilder<>(task);
    }

    public Result<S> execute(S state) {
        P taskPayload = stateProjector.apply(state);
        Result<V> taskResult = task.submit(taskPayload);

        if (taskResult.hasError()) {
            consumeIfNotNull(errorHandler, taskResult.error());
            return Result.error(taskResult.error());
        }

        consumeIfNotNull(successHandler, taskResult.value());

        return taskResult.toOptional()
                .flatMap(this::stateReducerFor)
                .map(state::reduce)
                .map(Result::ok)
                .orElse(Result.ok(state));
    }

    private static <T> void consumeIfNotNull(Consumer<T> consumer, T value) {
        if (Objects.nonNull(consumer)) {
            consumer.accept(value);
        }
    }

    private Optional<Consumer<S>> stateReducerFor(V value) {
        return Optional.ofNullable(stateReducer)
                .map(reducer -> state -> reducer.accept(state, value));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Step && ((Step<?, ?, ?>) obj).id.equals(id);
    }

    public String name() {
        return Optional.ofNullable(name).orElse("Unnamed");
    }

    public UUID id() {
        return id;
    }
}
