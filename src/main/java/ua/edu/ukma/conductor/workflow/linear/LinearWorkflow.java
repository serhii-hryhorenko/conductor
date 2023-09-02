package ua.edu.ukma.conductor.workflow.linear;

import ua.edu.ukma.conductor.task.PayloadType;
import ua.edu.ukma.conductor.task.ResultType;
import ua.edu.ukma.conductor.workflow.Workflow;
import ua.edu.ukma.conductor.workflow.WorkflowObserver;
import ua.edu.ukma.conductor.workflow.WorkflowState;
import ua.edu.ukma.conductor.workflow.step.Step;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

public class LinearWorkflow<S extends WorkflowState<S>> extends Workflow<S> {
    private final ListIterator<Step<ResultType, S, PayloadType>> stepsIterator;

    public LinearWorkflow(List<Step<ResultType, S, PayloadType>> steps, List<WorkflowObserver<S>> observers) {
        super(observers);
        this.stepsIterator = steps.listIterator();
    }

    @Override
    protected Optional<Step<ResultType, S, PayloadType>> nextStep() {
        if (stepsIterator.hasNext()) {
            return Optional.of(stepsIterator.next());
        }

        return Optional.empty();
    }
}
