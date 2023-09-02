package ua.edu.ukma.conductor.workflow.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.ukma.conductor.task.PayloadType;
import ua.edu.ukma.conductor.task.Result;
import ua.edu.ukma.conductor.task.ResultType;
import ua.edu.ukma.conductor.task.Task;
import ua.edu.ukma.conductor.workflow.WorkflowState;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class Step<V extends ResultType, S extends WorkflowState<S>, P extends PayloadType> {
    private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private final UUID uuid = UUID.randomUUID();

    private final Task<V, P> task;
    private final Function<S, P> stateProjector;

    private final BiConsumer<S, V> stateReducer;
    private final Consumer<V> successHandler;
    private final Consumer<Throwable> errorHandler;
    private final Consumer<Void> fallbackHandler;

    protected Step(Task<V, P> task,
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

    public static <V extends ResultType, S extends WorkflowState<S>, P extends PayloadType>
    StepBuilder<V, S, P> forTask(Task<V, P> task) {
        return new StepBuilder<>(task);
    }

    public Result<S> execute(S state) {
        logger.info("Is being executed.");
        logger.debug("Accepted state payload: {}", state);

        P taskPayload = stateProjector.apply(state);
        Result<V> executionResult = task.execute(taskPayload);

        onCompletion(executionResult);

        if (executionResult.isOk()) {
            Result<S> reducedState = stateReducerFor(executionResult.value())
                    .map(state::reduce)
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
