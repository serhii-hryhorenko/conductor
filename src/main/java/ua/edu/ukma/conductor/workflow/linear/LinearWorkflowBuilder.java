package ua.edu.ukma.conductor.workflow.linear;

import ua.edu.ukma.conductor.task.PayloadType;
import ua.edu.ukma.conductor.task.ResultType;
import ua.edu.ukma.conductor.workflow.WorkflowBuilder;
import ua.edu.ukma.conductor.workflow.WorkflowState;
import ua.edu.ukma.conductor.workflow.step.Step;

import java.util.ArrayList;
import java.util.List;

public class LinearWorkflowBuilder<S extends WorkflowState<S>> extends WorkflowBuilder<LinearWorkflowBuilder<S>, S> {
    private final List<Step<ResultType, S, PayloadType>> steps = new ArrayList<>();

    @Override
    public LinearWorkflowBuilder<S> addStep(Step<? extends ResultType, S, ? extends PayloadType> step) {
        steps.add((Step<ResultType, S, PayloadType>) step);

        return this;
    }

    @Override
    public LinearWorkflow<S> build() {
        return new LinearWorkflow<>(steps, observers());
    }
}
