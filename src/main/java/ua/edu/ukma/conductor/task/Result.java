package ua.edu.ukma.conductor.task;

import java.util.Objects;
import java.util.Optional;

public final class Result<T> {
    private final T value;
    private final Throwable error;

    private Result(T value, Throwable error) {
        this.value = value;
        this.error = error;
    }

    public static <T> Result<T> ok(T value) {
        return new Result<>(value, null);
    }

    public static <T> Result<T> error(Throwable error) {
        return new Result<>(null, error);
    }

    public Optional<T> toOptional() {
        return Optional.ofNullable(value);
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
