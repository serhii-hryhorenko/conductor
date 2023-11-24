package ua.edu.ukma.conductor.step.workflow.graph;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ua.edu.ukma.conductor.DefaultTestConfiguration;
import ua.edu.ukma.conductor.Workflows;
import ua.edu.ukma.conductor.observer.TestObserver;
import ua.edu.ukma.conductor.step.workflow.Step;
import ua.edu.ukma.conductor.step.workflow.TestState;
import ua.edu.ukma.conductor.step.workflow.TestStateProjection;
import ua.edu.ukma.conductor.step.workflow.Workflow;
import ua.edu.ukma.conductor.task.AsyncTask;
import ua.edu.ukma.conductor.task.Result;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ua.edu.ukma.conductor.observer.TestObserver.assertions;
import static ua.edu.ukma.conductor.state.StateMappers.noPayload;
import static ua.edu.ukma.conductor.state.StateMappers.workflowState;
import static ua.edu.ukma.conductor.step.workflow.graph.GraphWorkflowBuilder.thatDependsOn;

class GraphWorkflowTest extends DefaultTestConfiguration {
    private static final ScheduledExecutorService scheduledExecutorService =
        Executors.newSingleThreadScheduledExecutor();

    @Mock
    private Consumer<TestState> successHandler;

    @AfterAll
    static void cleanup() {
        scheduledExecutorService.shutdown();
    }

    @Test
    void testGraphWorkflow() {
        TestState initialState = new TestState("Amber", 34);

        String firstStepResult = "Jane";
        int secondStepResult = 5;
        TestState thirdStepResult = new TestState("Agnis", 16);

        AsyncTask<TestState, TestState> thirdTask = AsyncTask.fromFuture(
                () -> scheduledExecutorService.schedule(() -> thirdStepResult, 1L, TimeUnit.MILLISECONDS)
        );

        TestObserver<TestState> testStateTestObserver = assertions(
                (observer, state) -> assertThat(state).isEqualTo(initialState),
                (observer, state) -> assertThat(state.name()).isEqualTo(firstStepResult),
                (observer, state) -> assertThat(state.age()).isEqualTo(secondStepResult),
                (observer, state) -> assertThat(state).isEqualTo(thirdStepResult)
        );

        var firstStep = Step.<TestState, TestStateProjection, String>forTask(payload -> Result.of(firstStepResult))
                .thatAccepts(state -> new TestStateProjection(state.name()))
                .reducesState(TestState::setName)
                .build();
        var secondStep = Step.<TestState, Void, Integer>forTask(unused -> Result.of(secondStepResult))
                .thatAccepts(noPayload())
                .reducesState(TestState::setAge)
                .build();
        var thirdStep = Step.<TestState, TestState, TestState>forTask(thirdTask)
                .thatAccepts(workflowState())
                .reducesState((state, value) -> {
                    state.setName(value.name());
                    state.setAge(value.age());
                })
                .onSuccess(successHandler)
                .build();

        Workflow<TestState> workflow = Workflows.builder(firstStep)
                .addStep(thirdStep, thatDependsOn(firstStep, secondStep))
                .addStep(secondStep, thatDependsOn(firstStep))
                .attachObservers(testStateTestObserver)
                .build();

        testStateTestObserver.testWorkflow(workflow, initialState);

        verify(successHandler, times(1)).accept(any());
    }
}
