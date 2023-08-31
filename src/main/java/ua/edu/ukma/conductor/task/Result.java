package ua.edu.ukma.conductor.task;

import java.util.Objects;

public final class Result<T> {
    private final T value;
    private final Throwable error;

    private Result(T value, Throwable error) {
        this.value = value;
        this.error = error;
    }

    public static <T> Result<T> of(T value) {
        return new Result<>(value, null);
    }

    public static <T> Result<T> error(Throwable error) {
        return new Result<>(null, error);
    }

    public T value() {
        return value;
    }

    public boolean isOk() {
        return Objects.isNull(error);
    }

    public boolean hasError() {
        return !isOk();
    }

    public Throwable error() {
        Objects.requireNonNull(error);
        return error;
    }

    @Override
    public String toString() {
        return "Result{" + (isOk() ? "value=" + value : "error=" + error) + '}';
    }
}
