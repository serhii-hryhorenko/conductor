package ua.edu.ukma.conductor.step.workflow.linear;

import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.Step;
import ua.edu.ukma.conductor.step.workflow.WorkflowBuilder;

import java.util.ArrayList;
import java.util.List;

public class LinearWorkflowBuilder<S extends WorkflowState<S>> extends WorkflowBuilder<LinearWorkflowBuilder<S>, S> {
    private final List<Step<S>> steps = new ArrayList<>();

    @Override
    public LinearWorkflowBuilder<S> addStep(Step<S> step) {
        steps.add(step);

        return this;
    }

    @Override
    public LinearWorkflow<S> build() {
        return new LinearWorkflow<>(steps, observers());
    }
}
