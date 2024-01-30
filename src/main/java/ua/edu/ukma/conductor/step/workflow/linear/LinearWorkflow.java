package ua.edu.ukma.conductor.step.workflow.linear;

import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.WorkflowStep;
import ua.edu.ukma.conductor.step.workflow.Workflow;
import ua.edu.ukma.conductor.step.workflow.WorkflowObserver;
import ua.edu.ukma.conductor.task.Result;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class LinearWorkflow<S extends WorkflowState<S>> extends Workflow<S> {
    private final Iterator<WorkflowStep<S>> stepsIterator;

    public LinearWorkflow(List<WorkflowStep<S>> steps, List<WorkflowObserver<S>> observers) {
        super(observers);
        this.stepsIterator = steps.iterator();
    }

    @Override
    public Result<S> execute(S initialState) {
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

        return Result.ok(currentState);
    }

    private Optional<WorkflowStep<S>> nextStep() {
        if (stepsIterator.hasNext()) {
            return Optional.of(stepsIterator.next());
        }

        return Optional.empty();
    }
}
