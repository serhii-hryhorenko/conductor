package ua.edu.ukma.conductor.workflow.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.ukma.conductor.task.Result;
import ua.edu.ukma.conductor.task.Task;
import ua.edu.ukma.conductor.workflow.WorkflowState;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class Step<T, S extends WorkflowState<S>, P> {
    private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    private final Task<T, P> task;
    private final Function<S, P> stateProjector;

    private final BiConsumer<S, T> stateReducer;
    private final Consumer<T> successHandler;
    private final Consumer<Throwable> errorHandler;
    private final Consumer<Void> fallbackHandler;

    protected Step(Task<T, P> task,
                   Function<S, P> stateProjector,
                   BiConsumer<S, T> stateReducer,
                   Consumer<T> successHandler,
                   Consumer<Throwable> errorHandler,
                   Consumer<Void> fallbackHandler) {
        this.task = task;
        this.stateProjector = stateProjector;
        this.stateReducer = stateReducer;
        this.successHandler = successHandler;
        this.errorHandler = errorHandler;
        this.fallbackHandler = fallbackHandler;
    }

    public static <T, S extends WorkflowState<S>, P> StepBuilder<T, S, P> forTask(Task<T, P> task) {
        return new StepBuilder<>(task);
    }

    public Result<S> execute(S state) {
        logger.info("Is being executed.");
        logger.debug("Accepted state payload: {}", state);

        P taskPayload = stateProjector.apply(state);
        Result<T> executionResult = task.execute(taskPayload);

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

    private void onCompletion(Result<T> result) {
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

    private Optional<Consumer<S>> stateReducerFor(T value) {
        return Optional.ofNullable(stateReducer)
                .map(reducer -> state -> reducer.accept(state, value));
    }
}
