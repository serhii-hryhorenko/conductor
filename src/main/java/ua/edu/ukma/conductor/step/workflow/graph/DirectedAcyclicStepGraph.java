package ua.edu.ukma.conductor.step.workflow.graph;

import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.Step;

import java.util.*;

public class DirectedAcyclicStepGraph<S extends WorkflowState<S>> {
    private final Map<Step<S>, List<Step<S>>> adjacencyList;
    private final Step<S> startVertex;

    DirectedAcyclicStepGraph(Step<S> firstStep) {
        this.adjacencyList = new HashMap<>();
        this.startVertex = firstStep;
        addVertex(firstStep);
    }

    private void addVertex(Step<S> step) {
        adjacencyList.put(step, new ArrayList<>());
    }

    void addEdge(Step<S> from, Step<S> to) {
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

    List<Step<S>> topologicalSort() {
        List<Step<S>> sortedSteps = new ArrayList<>();
        Set<Step<S>> visited = new HashSet<>();

        topologicalSortHelper(startVertex, visited, sortedSteps);

        Collections.reverse(sortedSteps);
        return sortedSteps;
    }

    private void topologicalSortHelper(Step<S> step,
                                       Set<Step<S>> visited,
                                       List<Step<S>> sortedSteps) {
        visited.add(step);

        for (Step<S> adjacentStep : adjacencyList.getOrDefault(step, new ArrayList<>())) {
            if (!visited.contains(adjacentStep)) {
                topologicalSortHelper(adjacentStep, visited, sortedSteps);
            }
        }

        sortedSteps.add(step);
    }

    private boolean hasCycle() {
        HashSet<Step<S>> visited = new HashSet<>();

        return hasCycle(startVertex, visited);
    }

    private boolean hasCycle(Step<S> step,
                             HashSet<Step<S>> visited) {
        visited.add(step);
        List<Step<S>> neighbors = adjacencyList.get(step);

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
