package ua.edu.ukma.conductor.step.workflow.graph;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ua.edu.ukma.conductor.DefaultTestConfiguration;
import ua.edu.ukma.conductor.step.Step;
import ua.edu.ukma.conductor.step.workflow.TestState;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

class DirectedAcyclicStepGraphTest extends DefaultTestConfiguration {
    @Mock
    private Step<TestState> stepA;
    @Mock
    private Step<TestState> stepB;
    @Mock
    private Step<TestState> stepC;
    @Mock
    private Step<TestState> stepD;
    @Mock
    private Step<TestState> stepE;

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
        List<Step<TestState>> sortedSteps = graph.topologicalSort();

        // The correct topological order should be: A -> B -> C -> D
        assertThat(sortedSteps).containsExactlyElementsIn(List.of(stepA, stepB, stepC, stepD));
    }

    @Test
    void testAddEdge() {
        setupAcyclicGraph();
        graph.addEdge(stepD, stepE);

        List<Step<TestState>> sortedSteps = graph.topologicalSort();

        assertThat(sortedSteps).containsExactlyElementsIn(List.of(stepA, stepB, stepC, stepD, stepE));
    }

    @Test
    void testAddEdgeWithCycle() {
        setupAcyclicGraph();

        assertThrows(IllegalArgumentException.class, () -> graph.addEdge(stepD, stepA));
    }
}