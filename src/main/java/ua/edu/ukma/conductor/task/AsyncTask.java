package ua.edu.ukma.conductor.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public final class AsyncTask<V, P> implements Task<V, P> {
    private final Supplier<Future<V>> valueSupplier;

    private AsyncTask(Supplier<Future<V>> valueSupplier) {
        this.valueSupplier = valueSupplier;
    }

    public static <V, P>
    AsyncTask<V, P> fromFuture(Supplier<Future<V>> valueSupplier) {
        return new AsyncTask<>(valueSupplier);
    }

    @Override
    public Result<V> execute(P payload) {
        Future<V> future = valueSupplier.get();

        try {
            return Result.of(future.get());
        } catch (ExecutionException | InterruptedException e) {
            return Result.error(e);
        }
    }
}
