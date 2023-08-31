package ua.edu.ukma.conductor.workflow;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import ua.edu.ukma.conductor.DefaultTestConfiguration;
import ua.edu.ukma.conductor.task.Result;
import ua.edu.ukma.conductor.workflow.step.Step;

import java.util.List;
import java.util.Optional;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.*;


class WorkflowTest extends DefaultTestConfiguration {
    @Mock
    private Step<?, TestState, ?> step;
    @Mock
    private WorkflowObserver<TestState> observer;

    @Test
    void testStartWorkflow() {
        ArgumentCaptor<TestState> stateCaptor = ArgumentCaptor.forClass(TestState.class);
        TestState initialState = new TestState("Sarah", 32);

        when(step.execute(initialState))
                .thenReturn(Result.of(initialState));

        TestWorkflow testWorkflow = new TestWorkflow(step, List.of(observer));
        testWorkflow.start(initialState);

        verify(step, times(1)).execute(stateCaptor.capture());
        verify(observer, times(2)).observe(any());

        assertThat(stateCaptor.getValue()).isEqualTo(initialState);
    }

    private static class TestWorkflow extends Workflow<TestState> {
        private boolean executedStep;
        private final Step<?, TestState, ?> testStep;

        public TestWorkflow(Step<?, TestState, ?> testStep, List<WorkflowObserver<TestState>> observers) {
            super(observers);
            this.testStep = testStep;
        }

        @Override
        protected Optional<Step<?, TestState, ?>> nextStep() {
            if (executedStep) {
                return Optional.empty();
            }

            executedStep = true;
            return Optional.of(testStep);
        }
    }
}