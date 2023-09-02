package ua.edu.ukma.conductor.workflow.linear;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ua.edu.ukma.conductor.DefaultTestConfiguration;
import ua.edu.ukma.conductor.observer.TestObserver;
import ua.edu.ukma.conductor.task.AsyncTask;
import ua.edu.ukma.conductor.task.Result;
import ua.edu.ukma.conductor.task.ValueObject;
import ua.edu.ukma.conductor.task.Void;
import ua.edu.ukma.conductor.workflow.*;
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
import static ua.edu.ukma.conductor.task.ValueObject.wrap;

class LinearWorkflowTest extends DefaultTestConfiguration {
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    @Mock
    private Consumer<TestState> successHandler;

    @AfterAll
    static void cleanup() {
        scheduledExecutorService.shutdown();
    }

    @Test
    void testLinearWorkflow() {
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

        Workflow<TestState> workflow = Workflows.linearWorkflow(
                        Step.<ValueObject<String>, TestState, TestStateProjection>forTask(payload -> Result.of(wrap(firstStepResult)))
                                .thatAccepts(state -> new TestStateProjection(state.name()))
                                .reducingState((state, name) -> state.setName(name.value()))
                                .create(),

                        Step.<ValueObject<Integer>, TestState, Void>forTask(unused -> Result.of(wrap(secondStepResult)))
                                .thatAccepts(StateMappers.noPayload())
                                .reducingState((state, age) -> state.setAge(age.value()))
                                .create(),

                        Step.<TestState, TestState, TestState>forTask(thirdTask)
                                .thatAccepts(StateMappers.wholeState())
                                .reducingState((state, value) -> {
                                    state.setName(value.name());
                                    state.setAge(value.age());
                                })
                                .withSuccessHandler(successHandler)
                                .create()
                ).attachObservers(testStateTestObserver)
                .build();

        workflow.start(initialState);

        testStateTestObserver.checkExecutedSteps();
        verify(successHandler, times(1)).accept(any());
    }
}
