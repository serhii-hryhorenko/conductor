package ua.edu.ukma.conductor.workflow.graph;

import ua.edu.ukma.conductor.workflow.WorkflowState;
import ua.edu.ukma.conductor.workflow.step.Step;

import java.util.*;

public class DirectedAcyclicStepGraph<S extends WorkflowState<S>> {
    private final Map<Step<?, S, ?>, List<Step<?, S, ?>>> adjacencyList;
    private final Step<?, S, ?> startVertex;

    DirectedAcyclicStepGraph(Step<?, S, ?> firstStep) {
        this.adjacencyList = new HashMap<>();
        this.startVertex = firstStep;
        addVertex(firstStep);
    }

    void addVertex(Step<?, S, ?> step) {
        adjacencyList.put(step, new ArrayList<>());
    }

    void addEdge(Step<?, S, ?> fromStep, Step<?, S, ?> toStep) {
        if (!adjacencyList.containsKey(fromStep)) {
            addVertex(fromStep);
        }

        if (!adjacencyList.containsKey(toStep)) {
            addVertex(toStep);
        }

        adjacencyList.get(fromStep).add(toStep);
    }

    List<Step<?, S, ?>> topologicalSort() {
        List<Step<?, S, ?>> sortedSteps = new ArrayList<>();
        Set<Step<?, S, ?>> visited = new HashSet<>();

        topologicalSortHelper(startVertex, visited, sortedSteps);

        Collections.reverse(sortedSteps);
        return sortedSteps;
    }

    private void topologicalSortHelper(Step<?, S, ?> step, Set<Step<?, S, ?>> visited, List<Step<?, S, ?>> sortedSteps) {
        visited.add(step);

        for (Step<?, S, ?> adjacentStep : adjacencyList.getOrDefault(step, new ArrayList<>())) {
            if (!visited.contains(adjacentStep)) {
                topologicalSortHelper(adjacentStep, visited, sortedSteps);
            }
        }

        sortedSteps.add(step);
    }
}
