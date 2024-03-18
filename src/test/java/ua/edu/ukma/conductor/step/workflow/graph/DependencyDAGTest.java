package ua.edu.ukma.conductor.step.workflow.graph;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ua.edu.ukma.conductor.DefaultTestConfiguration;
import ua.edu.ukma.conductor.step.WorkflowStep;
import ua.edu.ukma.conductor.step.workflow.TestState;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

class DependencyDAGTest extends DefaultTestConfiguration {
    @Mock
    private WorkflowStep<TestState> stepA;
    @Mock
    private WorkflowStep<TestState> stepB;
    @Mock
    private WorkflowStep<TestState> stepC;
    @Mock
    private WorkflowStep<TestState> stepD;
    @Mock
    private WorkflowStep<TestState> stepE;

    private DependencyDAG<TestState> graph;

    void setupAcyclicGraph() {
        graph = new DependencyDAG<>();

        graph.addEdge(stepA, stepB);
        graph.addEdge(stepA, stepC);
        graph.addEdge(stepB, stepD);
        graph.addEdge(stepC, stepD);
    }

    @Test
    void testTopologicalSort() {
        setupAcyclicGraph();
        List<WorkflowStep<TestState>> sortedSteps = graph.topologicalSort();

        // The correct topological order should be: A -> B -> C -> D
        assertThat(sortedSteps).containsExactlyElementsIn(List.of(stepA, stepB, stepC, stepD));
    }

    @Test
    void testAddEdge() {
        setupAcyclicGraph();
        graph.addEdge(stepD, stepE);

        List<WorkflowStep<TestState>> sortedSteps = graph.topologicalSort();

        assertThat(sortedSteps).containsExactlyElementsIn(List.of(stepA, stepB, stepC, stepD, stepE));
    }

    @Test
    void testAddEdgeWithCycle() {
        setupAcyclicGraph();

        assertThrows(IllegalArgumentException.class, () -> graph.addEdge(stepD, stepA));
    }
}
