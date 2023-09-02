package ua.edu.ukma.conductor.task;

public class ValueObject<T> implements PayloadType, ResultType {
    private final T value;


    protected ValueObject(T value) {
        this.value = value;
    }

    public static <T> ValueObject<T> wrap(T value) {
        return new ValueObject<>(value);
    }

    public T value() {
        return value;
    }
}
