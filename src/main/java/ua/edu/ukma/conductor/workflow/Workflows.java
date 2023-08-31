package ua.edu.ukma.conductor.workflow;

import ua.edu.ukma.conductor.workflow.linear.LinearWorkflowBuilder;
import ua.edu.ukma.conductor.workflow.step.Step;

import java.util.Arrays;

public final class Workflows {
    private Workflows() {
    }

    @SafeVarargs
    public static <S extends WorkflowState<S>>
    WorkflowBuilder<LinearWorkflowBuilder<S>, S> linearWorkflow(Step<?, S, ?>... steps) {
        LinearWorkflowBuilder<S> builder = new LinearWorkflowBuilder<>();
        Arrays.stream(steps).sequential().forEach(builder::addStep);

        return builder;
    }
}
