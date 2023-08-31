package ua.edu.ukma.conductor.workflow;


import ua.edu.ukma.conductor.task.Result;
import ua.edu.ukma.conductor.workflow.step.Step;

import java.util.List;
import java.util.Optional;

public abstract class Workflow<S extends WorkflowState<S>> {
    private final List<WorkflowObserver<S>> observers;

    protected Workflow(List<WorkflowObserver<S>> observers) {
        this.observers = observers;
    }

    public final Result<S> start(S initialState) {
        S currentState = initialState;
        observers.forEach(observer -> observer.observe(initialState.copy()));

        for (Optional<Step<?, S, ?>> nextStep = nextStep(); nextStep.isPresent(); nextStep = nextStep()) {
            Step<?, S, ?> currentStep = nextStep.get();

            Result<S> reducedState = currentStep.execute(currentState);

            if (reducedState.hasError()) {
                return Result.error(reducedState.error());
            }

            observers.forEach(observer -> observer.observe(reducedState.value().copy()));
            currentState = reducedState.value();
        }

        return Result.of(currentState);
    }

    protected abstract Optional<Step<?, S, ?>> nextStep();
}
