package ua.edu.ukma.conductor.workflow.graph;

import ua.edu.ukma.conductor.task.PayloadType;
import ua.edu.ukma.conductor.task.ResultType;
import ua.edu.ukma.conductor.workflow.WorkflowState;
import ua.edu.ukma.conductor.workflow.step.Step;

import java.util.*;

public class DirectedAcyclicStepGraph<S extends WorkflowState<S>> {
    private final Map<Step<ResultType, S, PayloadType>, List<Step<ResultType, S, PayloadType>>> adjacencyList;
    private final Step<ResultType, S, PayloadType> startVertex;

    DirectedAcyclicStepGraph(Step<? extends ResultType, S, ? extends PayloadType> firstStep) {
        this.adjacencyList = new HashMap<>();
        Step<ResultType, S, PayloadType> initialStep = castStep(firstStep);
        this.startVertex = initialStep;
        addVertex(initialStep);
    }

    private void addVertex(Step<ResultType, S, PayloadType> step) {
        adjacencyList.put(castStep(step), new ArrayList<>());
    }

    void addEdge(Step<? extends ResultType, S, ? extends PayloadType> from,
                 Step<? extends ResultType, S, ? extends PayloadType> to) {
        Step<ResultType, S, PayloadType> castedFrom = castStep(from);
        Step<ResultType, S, PayloadType> castedTo = castStep(to);

        if (!adjacencyList.containsKey(castedFrom)) {
            addVertex(castedFrom);
        }

        if (!adjacencyList.containsKey(castedTo)) {
            addVertex(castedTo);
        }

        adjacencyList.get(castedFrom).add(castedTo);

        if (hasCycle()) {
            throw new IllegalArgumentException("Adding this edge would create a cycle in the graph.");
        }
    }

    List<Step<ResultType, S, PayloadType>> topologicalSort() {
        List<Step<ResultType, S, PayloadType>> sortedSteps = new ArrayList<>();
        Set<Step<ResultType, S, PayloadType>> visited = new HashSet<>();

        topologicalSortHelper(startVertex, visited, sortedSteps);

        Collections.reverse(sortedSteps);
        return sortedSteps;
    }

    private void topologicalSortHelper(Step<ResultType, S, PayloadType> step, Set<Step<ResultType, S, PayloadType>> visited, List<Step<ResultType, S, PayloadType>> sortedSteps) {
        visited.add(step);

        for (Step<ResultType, S, PayloadType> adjacentStep : adjacencyList.getOrDefault(step, new ArrayList<>())) {
            if (!visited.contains(adjacentStep)) {
                topologicalSortHelper(adjacentStep, visited, sortedSteps);
            }
        }

        sortedSteps.add(step);
    }

    private boolean hasCycle() {
        HashSet<Step<ResultType, S, PayloadType>> visited = new HashSet<>();

        return hasCycle(startVertex, visited);
    }

    private boolean hasCycle(Step<ResultType, S, PayloadType> step,
                             HashSet<Step<ResultType, S, PayloadType>> visited) {
        visited.add(step);
        List<Step<ResultType, S, PayloadType>> neighbors = adjacencyList.get(step);

        for (var neighbor : neighbors) {
            if (visited.contains(neighbor)) {
                return true;
            } else {
                return hasCycle(neighbor, visited);
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private Step<ResultType, S, PayloadType> castStep(Step<? extends ResultType, S, ? extends PayloadType> step) {
        return (Step<ResultType, S, PayloadType>) step;
    }
}
