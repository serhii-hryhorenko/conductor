package ua.edu.ukma.conductor.task;

@FunctionalInterface
public interface Task<T, P> {
    Result<T> execute(P payload);
}
