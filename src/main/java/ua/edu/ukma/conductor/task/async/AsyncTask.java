package ua.edu.ukma.conductor.task.async;

import ua.edu.ukma.conductor.task.Result;
import ua.edu.ukma.conductor.task.Task;

import java.util.concurrent.CompletableFuture;

public class AsyncTask<P, V> implements Task<P, V> {
    private final AsyncResultSupplier<P, V> resultSupplier;

    private AsyncTask(AsyncResultSupplier<P, V> resultSupplier) {
        this.resultSupplier = resultSupplier;
    }

    /**
     * Creates task object from a result sink.
     */
    public static <P, V> AsyncTask<P, V> from(AsyncResultSupplier<P, V> asyncTask) {
        return new AsyncTask<>(asyncTask);
    }

    /**
     * Creates task object from a result sink.
     * @param sink the sink to accept result
     * @param <V> the type of the result
     */
    public static <V> Result<V> await(ResultSink<V> sink) {
        AsyncResultSupplier<Void, V> asyncTask = (unused, result) -> sink.accept(result);

        return from(asyncTask).execute(null);
    }

    /**
     * Executes async task and returns the result.
     * @param payload required argument to complete the task
     * @return the result of the task
     */
    @Override
    public Result<V> execute(P payload) {
        CompletableFuture<Result<V>> futureResult = new CompletableFuture<>();
        resultSupplier.accept(payload, futureResult);

        try {
            return futureResult.get();
        } catch (Exception e) {
            return Result.error(e);
        }
    }
}
