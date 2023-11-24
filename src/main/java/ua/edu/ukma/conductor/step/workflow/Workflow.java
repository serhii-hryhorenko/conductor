package ua.edu.ukma.conductor.step.workflow;


import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.WorkflowStep;
import ua.edu.ukma.conductor.task.Result;

import java.util.List;
import java.util.Optional;

public abstract class Workflow<S extends WorkflowState<S>> extends WorkflowStep<S> {
    private final List<WorkflowObserver<S>> observers;

    protected Workflow(List<WorkflowObserver<S>> observers) {
        this.observers = observers;
    }

    @Override
    public final Result<S> execute(S initialState) {
        S currentState = initialState;
        notifyObservers(initialState);

        for (Optional<WorkflowStep<S>> step = nextStep(); step.isPresent(); step = nextStep()) {
            WorkflowStep<S> currentStep = step.get();
            Result<S> reducedState = currentStep.execute(currentState);

            if (reducedState.hasError()) {
                return Result.error(reducedState.error());
            }

            notifyObservers(reducedState.value());
            currentState = reducedState.value();
        }

        return Result.of(currentState);
    }

    protected abstract Optional<WorkflowStep<S>> nextStep();

    private void notifyObservers(S state) {
        observers.forEach(observer -> observer.observe(state.copy()));
    }
}
