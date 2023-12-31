package ua.edu.ukma.conductor.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {
    @Test
    void testSuccessResultWithValue() {
        Integer expectedValue = 42;

        Result<Integer> result = Result.of(expectedValue);

        assertTrue(result.isOk());
        assertFalse(result.hasError());
        assertEquals(expectedValue, result.value());
    }

    @Test
    void testErrorResult() {
        Throwable expectedError = new RuntimeException("Test error");

        Result<Object> result = Result.error(expectedError);

        assertFalse(result.isOk());
        assertTrue(result.hasError());
        assertSame(expectedError, result.error());
    }

    @Test
    void testErrorResultNullError() {
        assertThrows(NullPointerException.class, () -> Result.of(5).error());
    }
}