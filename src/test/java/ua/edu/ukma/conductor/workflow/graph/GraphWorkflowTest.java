package ua.edu.ukma.conductor.workflow.graph;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ua.edu.ukma.conductor.DefaultTestConfiguration;
import ua.edu.ukma.conductor.observer.TestObserver;
import ua.edu.ukma.conductor.task.AsyncTask;
import ua.edu.ukma.conductor.task.Result;
import ua.edu.ukma.conductor.workflow.TestState;
import ua.edu.ukma.conductor.workflow.TestStateProjection;
import ua.edu.ukma.conductor.workflow.Workflow;
import ua.edu.ukma.conductor.workflow.step.Step;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ua.edu.ukma.conductor.observer.TestObserver.assertions;
import static ua.edu.ukma.conductor.workflow.StateMappers.noPayload;
import static ua.edu.ukma.conductor.workflow.StateMappers.wholeState;
import static ua.edu.ukma.conductor.workflow.graph.GraphWorkflowBuilder.thatDependsOn;

class GraphWorkflowTest extends DefaultTestConfiguration {
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

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

        Step<String, TestState, TestStateProjection> firstStep = Step.<String, TestState, TestStateProjection>forTask(payload -> Result.of(firstStepResult))
                .thatAccepts(state -> new TestStateProjection(state.name()))
                .reducingState(TestState::setName)
                .create();
        Step<Integer, TestState, Void> secondStep = Step.<Integer, TestState, Void>forTask(unused -> Result.of(secondStepResult))
                .thatAccepts(noPayload())
                .reducingState(TestState::setAge)
                .create();
        Step<TestState, TestState, TestState> thirdStep = Step.<TestState, TestState, TestState>forTask(thirdTask)
                .thatAccepts(wholeState())
                .reducingState((state, value) -> {
                    state.setName(value.name());
                    state.setAge(value.age());
                })
                .withSuccessHandler(successHandler)
                .create();

        Workflow<TestState> workflow = new GraphWorkflowBuilder<>(firstStep)
                .addStep(thirdStep, thatDependsOn(firstStep, secondStep))
                .addStep(secondStep, thatDependsOn(firstStep))
                .attachObservers(testStateTestObserver)
                .build();


        workflow.start(initialState);

        testStateTestObserver.checkExecutedSteps();
        verify(successHandler, times(1)).accept(any());
    }
}
