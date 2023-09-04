package ua.edu.ukma.conductor.task;

/**
 * Encapsulates a call to any service, server, microservice
 * that potentially results in some value or error.
 *
 * @param <P> Payload type
 * @param <V> Result value type
 */
@FunctionalInterface
public interface Task<P, V> {
    /**
     * Submits the task to the receiver and awaits of its completion.
     *
     * @param payload required argument to complete the task
     */
    Result<V> submit(P payload);
}
