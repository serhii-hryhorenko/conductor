package ua.edu.ukma.conductor.step.workflow.linear;


import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.WorkflowStep;
import ua.edu.ukma.conductor.step.workflow.Workflow;
import ua.edu.ukma.conductor.step.workflow.WorkflowObserver;
import ua.edu.ukma.conductor.task.Result;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ParallelWorkflow<S extends WorkflowState<S>> extends Workflow<S> {
    private final List<WorkflowStep<S>> steps;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private S state;

    public ParallelWorkflow(List<WorkflowStep<S>> steps, List<WorkflowObserver<S>> observers) {
        super(observers);
        this.steps = steps;
    }

    @Override
    public Result<S> execute(S initialState) {
        setState(initialState);

        List<CompletableFuture<Result<S>>> executedSteps = steps.stream()
                .map(this::executeStep)
                .toList();

        for (CompletableFuture<Result<S>> executedStep : executedSteps) {
            Result<S> stepResult = executedStep.join();

            if (stepResult.hasError()) {
                return stepResult;
            }
        }

        return Result.ok(getState());
    }

    private CompletableFuture<Result<S>> executeStep(WorkflowStep<S> step) {
        return CompletableFuture.supplyAsync(() -> {
            Result<S> result = step.execute(getState());

            if (result.hasError()) {
                return result;
            }

            S reducedState = result.unwrap();
            setState(reducedState);
            notifyObservers(reducedState);

            return result;
        });
    }

    private void setState(S newState) {
        lock.writeLock().lock();
        state = newState;
        lock.writeLock().unlock();
    }

    private S getState() {
        lock.readLock().lock();
        S currentState = state;
        lock.readLock().unlock();

        return currentState;
    }
}
