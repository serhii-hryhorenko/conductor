package ua.edu.ukma.conductor;

import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.Step;
import ua.edu.ukma.conductor.step.StepOrBuilder;
import ua.edu.ukma.conductor.step.workflow.WorkflowBuilder;
import ua.edu.ukma.conductor.step.workflow.graph.GraphWorkflowBuilder;
import ua.edu.ukma.conductor.step.workflow.linear.LinearWorkflowBuilder;

import java.util.Arrays;

public final class Workflows {
    private Workflows() {
    }

    @SafeVarargs
    public static <S extends WorkflowState<S>>
    WorkflowBuilder<LinearWorkflowBuilder<S>, S> linearWorkflow(StepOrBuilder<S>... steps) {
        LinearWorkflowBuilder<S> builder = new LinearWorkflowBuilder<>();
        Arrays.stream(steps).sequential().forEach(builder::addStep);

        return builder;
    }

    public static <S extends WorkflowState<S>>
    GraphWorkflowBuilder<S> builder(Step<S> initialStep) {
        return new GraphWorkflowBuilder<>(initialStep);
    }
}