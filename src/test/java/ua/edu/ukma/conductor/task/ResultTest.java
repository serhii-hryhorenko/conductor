package ua.edu.ukma.conductor.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {
    @Test
    void testSuccessResultWithValue() {
        Integer expectedValue = 42;

        Result<Integer> result = Result.ok(expectedValue);

        assertTrue(result.isOk());
        assertFalse(result.hasError());
        assertEquals(expectedValue, result.unwrap());
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
        assertThrows(NullPointerException.class, () -> Result.ok(5).error());
    }
}