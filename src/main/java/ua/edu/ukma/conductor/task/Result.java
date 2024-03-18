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

    /**
     * Creates a successful result with a value.
     * @param value the value
     * @param <T> the type of the value
     */
    public static <T> Result<T> ok(T value) {
        return new Result<>(value, null);
    }

    /**
     * Creates a successful result without a value.
     * @return the result
     */
    public static Result<Void> ok() {
        return new Result<>(null, null);
    }

    /**
     * Creates a failed result with an error.
     * @param error the error
     * @param <T> the type of the value
     */
    public static <T> Result<T> error(Throwable error) {
        return new Result<>(null, error);
    }

    /**
     * Converts the result to an optional.
     */
    public Optional<T> toOptional() {
        return Optional.ofNullable(value);
    }

    /**
     * Unwraps the value from the result.
     */
    public T unwrap() {
        return value;
    }

    /**
     * Checks if the result is successful.
     */
    public boolean isOk() {
        return Objects.isNull(error);
    }

    /**
     * Checks if the result is failed.
     */
    public boolean hasError() {
        return !isOk();
    }

    /**
     * Gets the error from the result.
     */
    public Throwable error() {
        Objects.requireNonNull(error);
        return error;
    }

    @Override
    public String toString() {
        return "Result{" + (isOk() ? "value=" + value : "error=" + error) + '}';
    }
}
