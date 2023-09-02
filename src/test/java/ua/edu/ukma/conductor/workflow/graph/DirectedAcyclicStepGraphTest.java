package ua.edu.ukma.conductor.workflow.graph;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ua.edu.ukma.conductor.DefaultTestConfiguration;
import ua.edu.ukma.conductor.task.PayloadType;
import ua.edu.ukma.conductor.task.ResultType;
import ua.edu.ukma.conductor.workflow.TestState;
import ua.edu.ukma.conductor.workflow.step.Step;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

class DirectedAcyclicStepGraphTest extends DefaultTestConfiguration {
    @Mock
    private Step<ResultType, TestState, PayloadType> stepA;
    @Mock
    private Step<ResultType, TestState, PayloadType> stepB;
    @Mock
    private Step<ResultType, TestState, PayloadType> stepC;
    @Mock
    private Step<ResultType, TestState, PayloadType> stepD;
    @Mock
    private Step<ResultType, TestState, PayloadType> stepE;

    private DirectedAcyclicStepGraph<TestState> graph;

    void setupAcyclicGraph() {
        graph = new DirectedAcyclicStepGraph<>(stepA);

        graph.addEdge(stepA, stepB);
        graph.addEdge(stepA, stepC);
        graph.addEdge(stepB, stepD);
        graph.addEdge(stepC, stepD);
    }

    @Test
    void testTopologicalSort() {
        setupAcyclicGraph();
        List<Step<ResultType, TestState, PayloadType>> sortedSteps = graph.topologicalSort();

        // The correct topological order should be: Step A -> Step B -> Step C -> Step D
        assertThat(sortedSteps).containsExactlyElementsIn(List.of(stepA, stepB, stepC, stepD));
    }

    @Test
    void testAddEdge() {
        setupAcyclicGraph();
        graph.addEdge(stepD, stepE);

        List<Step<ResultType, TestState, PayloadType>> sortedSteps = graph.topologicalSort();

        assertThat(sortedSteps).containsExactlyElementsIn(List.of(stepA, stepB, stepC, stepD, stepE));
    }

    @Test
    void testAddEdgeWithCycle() {
        setupAcyclicGraph();

        assertThrows(IllegalArgumentException.class, () -> graph.addEdge(stepD, stepA));
    }
}