package ua.edu.ukma.conductor.task.async;

import ua.edu.ukma.conductor.task.Result;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * A supplier of asynchronous result.
 * @param <P> the type of the payload
 * @param <V> the type of the result
 */
public interface AsyncResultSupplier<P, V> extends BiConsumer<P, CompletableFuture<Result<V>>> {
}
