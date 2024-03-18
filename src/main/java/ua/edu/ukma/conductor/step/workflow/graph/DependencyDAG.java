package ua.edu.ukma.conductor.step.workflow.graph;

import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.WorkflowStep;

import java.util.*;
import java.util.stream.Collectors;

public final class DependencyDAG<S extends WorkflowState<S>> {
    private final Map<WorkflowStep<S>, List<WorkflowStep<S>>> adjacencyList;

    DependencyDAG() {
        this.adjacencyList = new HashMap<>();
    }

    /**
     * Returns the adjacent vertices of the given vertex.
     */
    private List<WorkflowStep<S>> adjacentSteps(WorkflowStep<S> vertex) {
        return adjacencyList.get(vertex);
    }

    /**
     * Returns the vertices that depend on the given vertex and ready to be executed.
     */
    public List<WorkflowStep<S>> dependentSteps(WorkflowStep<S> step) {
        // We need to get the adjacent vertices of the given vertex.
        // Then we need to ensure that the adjacent step is ready to be executed
        // (i.e. all of its dependencies have been executed).

        List<WorkflowStep<S>> adjacent = adjacentSteps(step).stream().toList();
        Set<WorkflowStep<S>> adjacentDependants = adjacent.stream()
                .flatMap(adj -> adjacentSteps(adj).stream())
                .collect(Collectors.toSet());

        return adjacent.stream()
                .filter(adj -> !adjacentDependants.contains(adj))
                .toList();
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

    void addVertex(WorkflowStep<S> step) {
        adjacencyList.put(step, new ArrayList<>());
    }

    WorkflowStep<S> startVertex() {
        return adjacencyList.keySet().stream()
                .filter(step -> adjacencyList.values().stream().noneMatch(dependant -> dependant.contains(step)))
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
        if (!(o instanceof DependencyDAG<?> that)) return false;

        return adjacencyList.equals(that.adjacencyList);
    }

    @Override
    public int hashCode() {
        return 31 * adjacencyList.hashCode();
    }
}
