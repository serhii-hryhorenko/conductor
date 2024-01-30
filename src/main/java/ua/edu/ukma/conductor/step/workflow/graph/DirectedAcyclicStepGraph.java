package ua.edu.ukma.conductor.step.workflow.graph;

import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.WorkflowStep;

import java.util.*;

public class DirectedAcyclicStepGraph<S extends WorkflowState<S>> {
    private final Map<WorkflowStep<S>, List<WorkflowStep<S>>> adjacencyList;
    private final WorkflowStep<S> startVertex;

    DirectedAcyclicStepGraph(WorkflowStep<S> firstStep) {
        this.adjacencyList = new HashMap<>();
        this.startVertex = firstStep;
        addVertex(firstStep);
    }

    private void addVertex(WorkflowStep<S> step) {
        adjacencyList.put(step, new ArrayList<>());
    }

    List<WorkflowStep<S>> adjacentVertices(WorkflowStep<S> vertex) {
        return adjacencyList.getOrDefault(vertex, List.of());
    }

    WorkflowStep<S> startVertex() {
        return startVertex;
    }

    void addEdge(WorkflowStep<S> from, WorkflowStep<S> to) {
        if (!adjacencyList.containsKey(from)) {
            addVertex(from);
        }

        if (!adjacencyList.containsKey(to)) {
            addVertex(to);
        }

        adjacencyList.get(from).add(to);

        if (hasCycle()) {
            throw new IllegalArgumentException("Adding this edge would create a cycle in the graph.");
        }
    }

    List<WorkflowStep<S>> topologicalSort() {
        List<WorkflowStep<S>> sortedSteps = new ArrayList<>();
        Set<WorkflowStep<S>> visited = new HashSet<>();

        topologicalSortHelper(startVertex, visited, sortedSteps);

        Collections.reverse(sortedSteps);
        return sortedSteps;
    }

    private void topologicalSortHelper(WorkflowStep<S> step,
                                       Set<WorkflowStep<S>> visited,
                                       List<WorkflowStep<S>> sortedSteps) {
        visited.add(step);

        for (WorkflowStep<S> adjacentStep : adjacencyList.getOrDefault(step, new ArrayList<>())) {
            if (!visited.contains(adjacentStep)) {
                topologicalSortHelper(adjacentStep, visited, sortedSteps);
            }
        }

        sortedSteps.add(step);
    }

    private boolean hasCycle() {
        HashSet<WorkflowStep<S>> visited = new HashSet<>();

        return hasCycle(startVertex, visited);
    }

    private boolean hasCycle(WorkflowStep<S> step,
                             HashSet<WorkflowStep<S>> visited) {
        visited.add(step);
        List<WorkflowStep<S>> neighbors = adjacencyList.get(step);

        for (var neighbor : neighbors) {
            if (visited.contains(neighbor)) {
                return true;
            } else {
                return hasCycle(neighbor, visited);
            }
        }

        return false;
    }
}
