package ua.edu.ukma.conductor.observer;

import ua.edu.ukma.conductor.workflow.Workflow;
import ua.edu.ukma.conductor.workflow.WorkflowObserver;
import ua.edu.ukma.conductor.workflow.WorkflowState;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.fail;

public class TestObserver<S extends WorkflowState<S>> implements WorkflowObserver<S> {
    private final Iterator<BiConsumer<TestObserver<S>, S>> assertionIterator;
    private final int expectedExecutedStepsCount;
    private int executedAssertionsCount = 0;
    private int stepsExecuted = 0;

    private TestObserver(List<BiConsumer<TestObserver<S>, S>> assertions) {
        assertionIterator = assertions.iterator();
        expectedExecutedStepsCount = assertions.size();
    }

    @SafeVarargs
    public static <S extends WorkflowState<S>> TestObserver<S> assertions(BiConsumer<TestObserver<S>, S>... assertions) {
        return new TestObserver<>(Arrays.stream(assertions).toList());
    }

    public void testWorkflow(Workflow<S> workflow, S initialState) {
        workflow.execute(initialState);
        checkExecutedSteps();
    }

    @Override
    public void observe(S workflowState) {
        stepsExecuted++;

        if (assertionIterator.hasNext() && hasAssertionsToExecute()) {
            assertionIterator.next().accept(this, workflowState);
            executedAssertionsCount++;
        }
    }

    private void checkExecutedSteps() {
        if (badConfiguration()) {
            fail("""
                    Number of executed steps doesn't match the number of assertions.
                    Bad Test Observer configuration. Add/remove some assertions to test out each step.
                                        
                    Executed Assertions: %d, Executed Steps: %d
                    """.formatted(executedAssertionsCount, stepsExecuted));
        }
    }

    private boolean hasAssertionsToExecute() {
        return executedAssertionsCount < expectedExecutedStepsCount;
    }

    private boolean badConfiguration() {
        return executedAssertionsCount < stepsExecuted;
    }

    public int stepNumber() {
        return stepsExecuted;
    }
}
