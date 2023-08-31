package ua.edu.ukma.conductor.task;

import java.util.List;

public final class AggregatedTask<P> implements Task<Void, P> {
    private final List<Task<Void, P>> tasks;

    public AggregatedTask(List<Task<Void, P>> tasks) {
        this.tasks = tasks;
    }

    @Override
    public Result<Void> execute(P payload) {
        List<Result<Void>> results = tasks.stream()
                .map(task -> task.execute(payload))
                .toList();

        List<Throwable> errors = results.stream()
                .filter(Result::hasError)
                .map(Result::error)
                .toList();

        if (!errors.isEmpty()) {
            return Result.error(new AggregatedException(errors));
        }

        return Result.of(null);
    }
}
