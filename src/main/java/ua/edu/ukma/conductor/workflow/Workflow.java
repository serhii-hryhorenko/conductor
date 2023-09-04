package ua.edu.ukma.conductor.workflow;


import ua.edu.ukma.conductor.task.Result;

import java.util.List;
import java.util.Optional;

public abstract class Workflow<S extends WorkflowState<S>> extends Step<S> {
    private final List<WorkflowObserver<S>> observers;

    protected Workflow(List<WorkflowObserver<S>> observers) {
        this.observers = observers;
    }

    @Override
    public final Result<S> execute(S initialState) {
        S currentState = initialState;
        observeState(initialState);

        for (Optional<Step<S>> step = nextStep(); step.isPresent(); step = nextStep()) {
            Step<S> currentStep = step.get();
            Result<S> reducedState = currentStep.execute(currentState);

            if (reducedState.hasError()) {
                return Result.error(reducedState.error());
            }

            observeState(reducedState.value());
            currentState = reducedState.value();
        }

        return Result.of(currentState);
    }

    protected abstract Optional<Step<S>> nextStep();

    private void observeState(S state) {
        observers.forEach(observer -> observer.observe(state.copy()));
    }
}
