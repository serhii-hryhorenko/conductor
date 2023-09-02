package ua.edu.ukma.conductor.workflow;

import ua.edu.ukma.conductor.task.PayloadType;
import ua.edu.ukma.conductor.task.ResultType;
import ua.edu.ukma.conductor.workflow.graph.GraphWorkflowBuilder;
import ua.edu.ukma.conductor.workflow.linear.LinearWorkflowBuilder;
import ua.edu.ukma.conductor.workflow.step.Step;

import java.util.Arrays;

public final class Workflows {
    private Workflows() {
    }

    @SafeVarargs
    public static <S extends WorkflowState<S>>
    WorkflowBuilder<LinearWorkflowBuilder<S>, S> linearWorkflow(Step<? extends ResultType, S, ? extends PayloadType>... steps) {
        LinearWorkflowBuilder<S> builder = new LinearWorkflowBuilder<>();
        Arrays.stream(steps).sequential().forEach(builder::addStep);

        return builder;
    }

    public static <S extends WorkflowState<S>>
    GraphWorkflowBuilder<S> builder(Step<? extends ResultType, S, ? extends PayloadType> initialStep) {
        return new GraphWorkflowBuilder<>(initialStep);
    }
}
