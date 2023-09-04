package ua.edu.ukma.conductor.workflow;

import ua.edu.ukma.conductor.workflow.graph.GraphWorkflowBuilder;
import ua.edu.ukma.conductor.workflow.linear.LinearWorkflowBuilder;

import java.util.Arrays;

public final class Workflows {
    private Workflows() {
    }

    @SafeVarargs
    public static <S extends WorkflowState<S>>
    WorkflowBuilder<LinearWorkflowBuilder<S>, S> linearWorkflow(StepOrBuilder<S>... steps) {
        LinearWorkflowBuilder<S> builder = new LinearWorkflowBuilder<>();
        Arrays.stream(steps).sequential().forEach(stepOrBuilder -> builder.addStep(stepOrBuilder.step()));

        return builder;
    }

    public static <S extends WorkflowState<S>>
    GraphWorkflowBuilder<S> builder(Step<S> initialStep) {
        return new GraphWorkflowBuilder<>(initialStep);
    }
}
