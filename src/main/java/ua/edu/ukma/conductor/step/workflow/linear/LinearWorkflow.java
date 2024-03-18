package ua.edu.ukma.conductor.step.workflow.linear;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.WorkflowStep;
import ua.edu.ukma.conductor.step.workflow.Workflow;
import ua.edu.ukma.conductor.step.workflow.WorkflowObserver;
import ua.edu.ukma.conductor.task.Result;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public final class LinearWorkflow<S extends WorkflowState<S>> extends Workflow<S> {
    private static final Logger logger = LoggerFactory.getLogger(LinearWorkflow.class);

    private final Iterator<WorkflowStep<S>> stepsIterator;

    LinearWorkflow(String name, List<WorkflowStep<S>> steps, List<WorkflowObserver<S>> observers) {
        super(name, observers);
        this.stepsIterator = steps.iterator();
    }

    @Override
    public Result<S> execute(S initialState) {
        logger.info("[{}] – Starting workflow", name());

        S currentState = initialState;
        notifyObservers(initialState);

        for (Optional<WorkflowStep<S>> step = nextStep(); step.isPresent(); step = nextStep()) {
            WorkflowStep<S> currentStep = step.get();
            Result<S> reducedState = currentStep.execute(currentState);

            if (reducedState.hasError()) {
                logger.error("[{}] – Workflow failed on step `{}` with error:",
                        name(), currentStep, reducedState.error());
                return Result.error(reducedState.error());
            }

            notifyObservers(reducedState.unwrap());
            currentState = reducedState.unwrap();

            logger.debug("[{}] – Step `{}` finished", name(), currentStep);
            logger.debug("[{}] – Current state: {}", name(), currentState);
        }

        logger.info("[{}] – Workflow finished", name());

        return Result.ok(currentState);
    }

    private Optional<WorkflowStep<S>> nextStep() {
        if (stepsIterator.hasNext()) {
            return Optional.of(stepsIterator.next());
        }

        return Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LinearWorkflow<?> that)) return false;

        return stepsIterator.equals(that.stepsIterator);
    }

    @Override
    public int hashCode() {
        return stepsIterator.hashCode();
    }
}
