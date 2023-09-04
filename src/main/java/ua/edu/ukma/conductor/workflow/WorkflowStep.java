package ua.edu.ukma.conductor.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.ukma.conductor.task.Result;
import ua.edu.ukma.conductor.task.Task;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class WorkflowStep<S extends WorkflowState<S>, P, V> extends Step<S> {
    private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private final UUID uuid = UUID.randomUUID();

    private final Task<P, V> task;
    private final Function<S, P> stateProjector;

    private final BiConsumer<S, V> stateReducer;
    private final Consumer<V> successHandler;
    private final Consumer<Throwable> errorHandler;
    private final Consumer<Void> fallbackHandler;

    protected WorkflowStep(Task<P, V> task,
                           Function<S, P> stateProjector,
                           BiConsumer<S, V> stateReducer,
                           Consumer<V> successHandler,
                           Consumer<Throwable> errorHandler,
                           Consumer<Void> fallbackHandler) {
        this.task = task;
        this.stateProjector = stateProjector;
        this.stateReducer = stateReducer;
        this.successHandler = successHandler;
        this.errorHandler = errorHandler;
        this.fallbackHandler = fallbackHandler;
    }

    public static <S extends WorkflowState<S>, P, V>
    WorkflowStepBuilder<S, P, V> forTask(Task<P, V> task) {
        return new WorkflowStepBuilder<>(task);
    }

    public Result<S> execute(S state) {
        logger.info("Is being executed.");
        logger.debug("Accepted state payload: {}", state);

        P taskPayload = stateProjector.apply(state);
        Result<V> executionResult = task.submit(taskPayload);

        onCompletion(executionResult);

        if (executionResult.isOk()) {
            Result<S> reducedState = stateReducerFor(executionResult.value())
                    .map(state::mutate)
                    .map(Result::of)
                    .orElseGet(() -> Result.of(state));

            logger.info("Completed successfully, reduced state: {}", state);

            return reducedState;
        }

        logger.error("Execution failed with error: {}", executionResult.error());
        return Result.error(executionResult.error());
    }

    void onFallback() {
        consumeIfNotNull(fallbackHandler, null);
    }

    private void onCompletion(Result<V> result) {
        if (result.hasError()) {
            consumeIfNotNull(errorHandler, result.error());
            return;
        }

        consumeIfNotNull(successHandler, result.value());
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
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
