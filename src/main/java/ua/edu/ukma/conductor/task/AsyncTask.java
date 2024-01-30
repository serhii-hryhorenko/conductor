package ua.edu.ukma.conductor.task;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class AsyncTask<P, V> implements Task<P, V> {
    private final Supplier<Future<V>> valueSupplier;

    private AsyncTask(Supplier<Future<V>> valueSupplier) {
        this.valueSupplier = valueSupplier;
    }

    public static <P, V>
    AsyncTask<P, V> fromFuture(Supplier<Future<V>> valueSupplier) {
        return new AsyncTask<>(valueSupplier);
    }

    public static <P, V>
    AsyncTask<P, V> fromFuture(Consumer<CompletableFuture<V>> futureConsumer) {
        return new AsyncTask<>(() -> {
            var future = new CompletableFuture<V>();
            futureConsumer.accept(future);

            return future;
        });
    }

    @Override
    public Result<V> submit(P payload) {
        Future<V> future = valueSupplier.get();

        try {
            return Result.ok(future.get());
        } catch (ExecutionException | InterruptedException e) {
            return Result.error(e);
        }
    }
}
