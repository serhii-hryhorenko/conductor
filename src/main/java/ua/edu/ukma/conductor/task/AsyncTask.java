package ua.edu.ukma.conductor.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public final class AsyncTask<T, P> implements Task<T, P> {
    private final Supplier<Future<T>> valueSupplier;

    private AsyncTask(Supplier<Future<T>> valueSupplier) {
        this.valueSupplier = valueSupplier;
    }

    public static <T, P> AsyncTask<T, P> fromFuture(Supplier<Future<T>> valueSupplier) {
        return new AsyncTask<>(valueSupplier);
    }

    @Override
    public Result<T> execute(P payload) {
        Future<T> future = valueSupplier.get();

        try {
            return Result.of(future.get());
        } catch (ExecutionException | InterruptedException e) {
            return Result.error(e);
        }
    }
}
