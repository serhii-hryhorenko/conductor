package ua.edu.ukma.conductor.workflow;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class WorkflowStateTest {

    @Test
    void testCopyMethod() {
        TestState initialState = new TestState("test", 19);
        TestState copiedState = initialState.copy();

        assertNotSame(initialState, copiedState);
        assertEquals(initialState.name(), copiedState.name());
        assertEquals(initialState.age(), copiedState.age());
    }

    @Test
    void testReduceMethod() {
        TestState initialState = new TestState("test", 42);
        Consumer<TestState> stateReducer = state -> state.setAge(19);

        TestState mutatedState = initialState.reduce(stateReducer);

        assertNotSame(initialState, mutatedState);
        assertEquals(19, mutatedState.age());
    }
}