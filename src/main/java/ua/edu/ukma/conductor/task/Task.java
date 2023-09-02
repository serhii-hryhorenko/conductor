package ua.edu.ukma.conductor.task;

@FunctionalInterface
public interface Task<V extends ResultType, P extends PayloadType> {
    Result<V> execute(P payload);
}
