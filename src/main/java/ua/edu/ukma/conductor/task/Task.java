package ua.edu.ukma.conductor.task;

@FunctionalInterface
public interface Task<V, P> {
    Result<V> execute(P payload);
}
