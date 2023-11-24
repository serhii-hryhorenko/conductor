package ua.edu.ukma.conductor.step.workflow.linear;

import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.Step;
import ua.edu.ukma.conductor.step.workflow.Workflow;
import ua.edu.ukma.conductor.step.workflow.WorkflowObserver;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

public class LinearWorkflow<S extends WorkflowState<S>> extends Workflow<S> {
    private final ListIterator<Step<S>> stepsIterator;

    public LinearWorkflow(List<Step<S>> steps, List<WorkflowObserver<S>> observers) {
        super(observers);
        this.stepsIterator = steps.listIterator();
    }

    @Override
    protected Optional<Step<S>> nextStep() {
        if (stepsIterator.hasNext()) {
            return Optional.of(stepsIterator.next());
        }

        return Optional.empty();
    }
}
