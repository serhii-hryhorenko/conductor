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
    private final Logger logger = LoggerFactory.getLogger(GraphWorkflow.class);

    private final DirectedAcyclicStepGraph<S> graph;

    private final ReentrantLock lock = new ReentrantLock();
    private S state;
    private boolean failed = false;

    GraphWorkflow(DirectedAcyclicStepGraph<S> graph, List<WorkflowObserver<S>> workflowObservers) {
        super(workflowObservers);
        this.graph = graph;
    }

    @Override
    public Result<S> execute(S initialState) {
        notifyObservers(initialState);

        Set<WorkflowStep<S>> visited = Collections.synchronizedSet(new HashSet<>());
        return executeGraph(initialState, graph.startVertex(), visited);
    }

    /**
     * Executes the graph recursively.
     */
    private Result<S> executeGraph(S state, WorkflowStep<S> currentStep, Set<WorkflowStep<S>> visited) {
        // If the step has already been visited, we can skip it.
        if (visited.contains(currentStep)) {
            logger.debug("Skipping step `{}`, because it has already been visited", currentStep);
            return Result.ok(state);
        }

        // If the workflow has failed, we can skip the rest of the steps.
        if (failed) {
            return Result.ok(state);
        }

        visited.add(currentStep);
        var result = currentStep.execute(state);

        if (result.hasError()) {
            fail();
            return result;
        }

        setState(result.value());
        notifyObservers(result.value().copy());

        CompletableFuture<Result<S>>[] futureResults = adjacentSteps(currentStep).stream()
                .map(step -> supplyAsync(() -> executeGraph(state(), step, visited)))
                .toArray(CompletableFuture[]::new);

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

    private List<WorkflowStep<S>> adjacentSteps(WorkflowStep<S> step) {
        return graph.adjacentVertices(step);
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
}
