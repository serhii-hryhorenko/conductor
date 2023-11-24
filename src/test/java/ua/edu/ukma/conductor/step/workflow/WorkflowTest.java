package ua.edu.ukma.conductor.step.workflow;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import ua.edu.ukma.conductor.DefaultTestConfiguration;
import ua.edu.ukma.conductor.step.WorkflowStep;
import ua.edu.ukma.conductor.task.Result;

import java.util.List;
import java.util.Optional;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.*;


class WorkflowTest extends DefaultTestConfiguration {
    @Mock
    private WorkflowStep<TestState> step;
    @Mock
    private WorkflowObserver<TestState> observer;

    private final TestState initialState = new TestState("Sarah", 32);

    @Test
    void testStartWorkflow() {
        setupStep();
        ArgumentCaptor<TestState> stateCaptor = ArgumentCaptor.forClass(TestState.class);

        TestWorkflow testWorkflow = new TestWorkflow(step, List.of(observer));
        testWorkflow.execute(initialState);

        verify(step, times(1)).execute(stateCaptor.capture());
        verify(observer, times(2)).observe(any());

        assertThat(stateCaptor.getValue()).isEqualTo(initialState);
    }

    @Test
    void testNestedWorkflow() {
        setupStep();

        TestWorkflow nested = spy(new TestWorkflow(step, List.of(observer)));
        TestWorkflow workflow = new TestWorkflow(nested, List.of(observer));

        workflow.execute(initialState);

        verify(nested, times(1)).execute(initialState);
        verify(observer, times(4)).observe(any());
    }

    private void setupStep() {
        when(step.execute(initialState))
                .thenReturn(Result.of(initialState));
    }

    private static class TestWorkflow extends Workflow<TestState> {
        private boolean executedStep;
        private final WorkflowStep<TestState> testStep;

        public TestWorkflow(WorkflowStep<TestState> testStep, List<WorkflowObserver<TestState>> observers) {
            super(observers);
            this.testStep = testStep;
        }

        @Override
        protected Optional<WorkflowStep<TestState>> nextStep() {
            if (executedStep) {
                return Optional.empty();
            }

            executedStep = true;
            return Optional.of(testStep);
        }
    }
}