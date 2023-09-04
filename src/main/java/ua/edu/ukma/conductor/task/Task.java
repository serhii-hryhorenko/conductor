package ua.edu.ukma.conductor.task;

@FunctionalInterface
public interface Task<P, V> {
    Result<V> execute(P payload);
}
