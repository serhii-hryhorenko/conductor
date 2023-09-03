package ua.edu.ukma.conductor.workflow.linear;

import ua.edu.ukma.conductor.workflow.Workflow;
import ua.edu.ukma.conductor.workflow.WorkflowObserver;
import ua.edu.ukma.conductor.workflow.WorkflowState;
import ua.edu.ukma.conductor.workflow.WorkflowStep;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

public class LinearWorkflow<S extends WorkflowState<S>> extends Workflow<S> {
    private final ListIterator<WorkflowStep<S>> stepsIterator;

    public LinearWorkflow(List<WorkflowStep<S>> steps, List<WorkflowObserver<S>> observers) {
        super(observers);
        this.stepsIterator = steps.listIterator();
    }

    @Override
    protected Optional<WorkflowStep<S>> nextStep() {
        if (stepsIterator.hasNext()) {
            return Optional.of(stepsIterator.next());
        }

        return Optional.empty();
    }
}
