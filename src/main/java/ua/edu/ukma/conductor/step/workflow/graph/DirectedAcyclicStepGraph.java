package ua.edu.ukma.conductor.step.workflow.graph;

import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.WorkflowStep;

import java.util.*;

public final    class DirectedAcyclicStepGraph<S extends WorkflowState<S>> {
    private final Map<WorkflowStep<S>, List<WorkflowStep<S>>> adjacencyList;

    DirectedAcyclicStepGraph() {
        this.adjacencyList = new HashMap<>();
    }

    private void addVertex(WorkflowStep<S> step) {
        adjacencyList.put(step, new ArrayList<>());
    }

    List<WorkflowStep<S>> adjacentVertices(WorkflowStep<S> vertex) {
        return adjacencyList.getOrDefault(vertex, List.of());
    }

    void addEdge(WorkflowStep<S> from, WorkflowStep<S> to) {
        if (!adjacencyList.containsKey(from)) {
            addVertex(from);
        }

        if (!adjacencyList.containsKey(to)) {
            addVertex(to);
        }

        adjacencyList.get(from).add(to);

        try {
            WorkflowStep<S> start = startVertex();

            if (hasCycle(start)) {
                String errMessage = String.format("`%s -> %s` creates a cycle in the graph.", from, to);
                throw new IllegalArgumentException(errMessage);
            }
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException("Graph has not a step to begin with (independent step).");
        }
    }

    WorkflowStep<S> startVertex() {
        return adjacencyList.keySet().stream()
                .filter(step -> adjacencyList.values().stream().noneMatch(list -> list.contains(step)))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Graph contains cycle."));
    }

    List<WorkflowStep<S>> topologicalSort() {
        List<WorkflowStep<S>> sortedSteps = new ArrayList<>();
        Set<WorkflowStep<S>> visited = new HashSet<>();

        WorkflowStep<S> startVertex = startVertex();

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

    private boolean hasCycle(WorkflowStep<S> start) {
        HashSet<WorkflowStep<S>> visited = new HashSet<>();

        return hasCycle(start, visited);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DirectedAcyclicStepGraph<?> that)) return false;

        return adjacencyList.equals(that.adjacencyList);
    }

    @Override
    public int hashCode() {
        return 31 * adjacencyList.hashCode();
    }
}
