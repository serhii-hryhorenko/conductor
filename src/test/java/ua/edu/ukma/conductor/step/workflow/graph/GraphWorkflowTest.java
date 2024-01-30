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
import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ua.edu.ukma.conductor.observer.TestObserver.assertions;
import static ua.edu.ukma.conductor.state.StateMappers.noPayload;
import static ua.edu.ukma.conductor.state.StateMappers.workflowState;
import static ua.edu.ukma.conductor.step.workflow.graph.GraphWorkflowBuilder.dependsOn;

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

        var firstStep = Step.<TestState, TestStateProjection, String>forTask(payload -> Result.ok(firstStepResult))
                .named("First step")
                .thatAccepts(state -> new TestStateProjection(state.name()))
                .reducesState(TestState::setName)
                .build();
        var secondStep = Step.<TestState, Void, Integer>forTask(unused -> Result.ok(secondStepResult))
                .named("Second step")
                .thatAccepts(noPayload())
                .reducesState(TestState::setAge)
                .build();
        var thirdStep = Step.<TestState, TestState, TestState>forTask(thirdTask)
                .named("Third step")
                .thatAccepts(workflowState())
                .reducesState((state, value) -> {
                    state.setName(value.name());
                    state.setAge(value.age());
                })
                .onSuccess(successHandler)
                .build();


        // https://dreampuf.github.io/GraphvizOnline/#digraph%20G%20%7B%0A%20%20%20%20%22First%20step%22%20-%3E%20%22Second%20step%22%3B%0A%20%20%20%20%22First%20step%22%20-%3E%20%22Third%20step%22%3B%0A%20%20%20%20%22Second%20step%22%20-%3E%20%22Third%20step%22%3B%0A%7D
        Workflow<TestState> workflow = Workflows.builder(firstStep)
                .addStep(thirdStep, dependsOn(firstStep, secondStep))
                .addStep(secondStep, dependsOn(firstStep))
                .attachObservers(testStateTestObserver)
                .synchronous()
                .build();

        testStateTestObserver.testWorkflow(workflow, initialState);

        verify(successHandler, times(1)).accept(any());
    }

    @Test
    void testAsyncGraphWorkflow() {
        TestState initialState = new TestState("Amber", 34);

        String firstStepResult = "Jane";
        int thirdStepResult = 5;
        TestState secondStepResult = new TestState("Agnis", 16);

        AsyncTask<TestState, TestState> secondTask = AsyncTask.fromFuture(
                () -> scheduledExecutorService.schedule(() -> secondStepResult, 1L, TimeUnit.SECONDS)
        );

        var firstStep = Step.<TestState, TestStateProjection, String>forTask(payload -> Result.ok(firstStepResult))
                .named("First step")
                .thatAccepts(state -> new TestStateProjection(state.name()))
                .reducesState(TestState::setName)
                .build();
        var secondStep = Step.<TestState, TestState, TestState>forTask(secondTask)
                .named("Third step")
                .thatAccepts(workflowState())
                .reducesState((state, value) -> {
                    state.setName(value.name());
                    state.setAge(value.age());
                })
                .build();
        var thirdStep = Step.<TestState, Void, Integer>forTask(unused -> Result.ok(thirdStepResult))
                .named("Second step")
                .thatAccepts(noPayload())
                .reducesState(TestState::setAge)
                .build();

        TestObserver<TestState> testStateTestObserver = assertions(
                (observer, state) -> assertThat(state).isEqualTo(initialState),
                (observer, state) -> assertThat(state.name()).isEqualTo(firstStepResult),
                (observer, state) -> assertThat(state.age()).isEqualTo(thirdStepResult),
                (observer, state) -> assertThat(state).isEqualTo(secondStepResult)
        );

        Workflow<TestState> workflow = Workflows.builder(firstStep)
                .addStep(thirdStep, dependsOn(firstStep, secondStep))
                .addStep(secondStep, dependsOn(firstStep))
                .attachObservers(testStateTestObserver)
                .build();

        testStateTestObserver.testWorkflow(workflow, initialState);
    }

    @Test
    void testFailingAsyncWorkflow() {
        var initialState = new TestState("Amber", 34);

        var firstStep = Step.<TestState, TestStateProjection, String>forTask(payload -> Result.ok("Jane"))
                .named("First step")
                .thatAccepts(state -> new TestStateProjection(state.name()))
                .reducesState(TestState::setName)
                .build();

        var error = new RuntimeException("Failed");
        var secondStep = Step.<TestState, Void, Integer>forTask(unused -> Result.error(error))
                .named("Second step")
                .thatAccepts(noPayload())
                .reducesState(TestState::setAge)
                .build();

        var thirdStep = Step.<TestState, TestState, TestState>forTask(unused -> Result.ok(initialState))
                .thatAccepts(workflowState())
                .reducesState((state, value) -> {
                    state.setName(value.name());
                    state.setAge(value.age());
                })
                .onSuccess(successHandler)
                .build();

        Workflow<TestState> workflow = Workflows.builder(firstStep)
            .addStep(secondStep, dependsOn(firstStep))
            .addStep(thirdStep, dependsOn(secondStep))
            .build();

        Result<TestState> result = workflow.execute(initialState);
        assertWithMessage("Workflow should fail")
            .that(result.hasError())
            .isTrue();

        assertWithMessage("Workflow should fail with error from second step")
            .that(result.error())
            .isEqualTo(error);
    }

    @Test
    void testFailingWorkflow() {
        var initialState = new TestState("Amber", 34);

        var firstStep = Step.<TestState, TestStateProjection, String>forTask(payload -> Result.ok("Jane"))
                .named("First step")
                .thatAccepts(state -> new TestStateProjection(state.name()))
                .reducesState(TestState::setName)
                .build();

        var error = new RuntimeException("Failed");
        var secondStep = Step.<TestState, Void, Integer>forTask(unused -> Result.error(error))
                .named("Second step")
                .thatAccepts(noPayload())
                .reducesState(TestState::setAge)
                .build();

        var thirdStep = Step.<TestState, TestState, TestState>forTask(unused -> Result.ok(initialState))
                .thatAccepts(workflowState())
                .reducesState((state, value) -> {
                    state.setName(value.name());
                    state.setAge(value.age());
                })
                .onSuccess(successHandler)
                .build();

        Workflow<TestState> workflow = Workflows.builder(firstStep)
            .addStep(secondStep, dependsOn(firstStep))
            .addStep(thirdStep, dependsOn(secondStep))
            .build();

        Result<TestState> result = workflow.execute(initialState);
        assertWithMessage("Workflow should fail")
            .that(result.hasError())
            .isTrue();

        assertWithMessage("Workflow should fail with error from second step")
            .that(result.error())
            .isEqualTo(error);
    }
}
