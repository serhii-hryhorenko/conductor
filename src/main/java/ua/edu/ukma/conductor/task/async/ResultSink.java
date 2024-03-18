package ua.edu.ukma.conductor.task.async;


import ua.edu.ukma.conductor.task.Result;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * A sink to accept result of an asynchronous task.
 * @param <V> the type of the result
 */
public interface ResultSink<V> extends Consumer<CompletableFuture<Result<V>>> {
}
