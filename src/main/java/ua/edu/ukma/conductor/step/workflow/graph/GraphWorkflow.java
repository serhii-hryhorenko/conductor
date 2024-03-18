package ua.edu.ukma.conductor.step.workflow.graph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.WorkflowStep;
import ua.edu.ukma.conductor.step.workflow.Workflow;
import ua.edu.ukma.conductor.step.workflow.WorkflowObserver;
import ua.edu.ukma.conductor.task.Result;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public final class GraphWorkflow<S extends WorkflowState<S>> extends Workflow<S> {
    private static final Logger logger = LoggerFactory.getLogger(GraphWorkflow.class);

    private final DependencyDAG<S> graph;

    private final ReentrantLock lock = new ReentrantLock();
    private S state;

    private boolean failed = false;

    GraphWorkflow(String name, DependencyDAG<S> graph, List<WorkflowObserver<S>> workflowObservers) {
        super(name, workflowObservers);
        this.graph = graph;
    }

    /**
     * Executes the workflow by traversing dependency graph.
     * @return The result of the workflow.
     */
    @Override
    public Result<S> execute(S initialState) {
        logger.info("[{}] – Starting workflow", name());
        notifyObservers(initialState);

        Set<WorkflowStep<S>> visited = Collections.synchronizedSet(new HashSet<>());

        Result<S> result = executeGraph(initialState, graph.startVertex(), visited);
        logger.info("[{}] – Workflow finished", name());

        return result;
    }

    /**
     * Executes the graph steps recursively.
     */
    private Result<S> executeGraph(S state, WorkflowStep<S> currentStep, Set<WorkflowStep<S>> visited) {
        // If the step has already been visited, we can skip it.
        if (visited.contains(currentStep)) {
            logger.info("[{}] – Skipping step `{}`, because it has already been visited", name(), currentStep);
            return Result.ok(state);
        }

        // If the workflow has failed, we can skip the rest of the steps.
        if (failed) {
            logger.info("[{}] – Skipping step `{}`, because the workflow has failed", name(), currentStep);
            return Result.ok(state);
        }

        visited.add(currentStep);
        var result = currentStep.execute(state);

        if (result.hasError()) {
            logger.error("[{}] – Step `{}` failed with error: ", name(), currentStep, result.error());
            fail();
            return result;
        }

        setState(result.unwrap());
        notifyObservers(result.unwrap().copy());

        logger.info("[{}] – Step `{}` finished successfully", name(), currentStep);
        logger.info("[{}] – Current state: {}", name(), state());

        CompletableFuture<Result<S>>[] futureResults = executeSubtasks(currentStep, visited);
        CompletableFuture.allOf(futureResults).join();

        for (CompletableFuture<Result<S>> futureResult : futureResults) {
            try {
                Result<S> stepResult = futureResult.get();
                if (stepResult.hasError()) {
                    return stepResult;
                }
            } catch (InterruptedException | ExecutionException e) {
                fail();
                return Result.error(e);
            }
        }

        return Result.ok(state());
    }

    private CompletableFuture<Result<S>>[] executeSubtasks(WorkflowStep<S> currentStep, Set<WorkflowStep<S>> visited) {
        CompletableFuture[] subtasks = graph.dependentSteps(currentStep).stream()
                .map(step -> supplyAsync(() -> executeGraph(state(), step, visited)))
                .toArray(CompletableFuture[]::new);

        return (CompletableFuture<Result<S>>[]) subtasks;
    }

    private void fail() {
        this.failed = true;
    }

    private void setState(S state) {
        lock.lock();

        try {
            this.state = state;
        } finally {
            lock.unlock();
        }
    }

    private S state() {
        lock.lock();

        try {
            return state;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GraphWorkflow<?> that)) return false;

        if (failed != that.failed) return false;
        return graph.equals(that.graph);
    }

    @Override
    public int hashCode() {
        int result = graph.hashCode();
        result = 31 * result + (failed ? 1 : 0);
        return result;
    }
}
