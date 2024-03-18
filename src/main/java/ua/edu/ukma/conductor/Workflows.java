package ua.edu.ukma.conductor;

import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.WorkflowStep;
import ua.edu.ukma.conductor.step.WorkflowStepOrBuilder;
import ua.edu.ukma.conductor.step.workflow.WorkflowBuilder;
import ua.edu.ukma.conductor.step.workflow.graph.GraphWorkflowBuilder;
import ua.edu.ukma.conductor.step.workflow.linear.LinearWorkflowBuilder;
import ua.edu.ukma.conductor.step.workflow.linear.ParallelWorkflow;

import java.util.Arrays;
import java.util.List;

public final class Workflows {
    private Workflows() {
    }

    @SafeVarargs
    public static <S extends WorkflowState<S>>
    WorkflowBuilder<LinearWorkflowBuilder<S>, S> sequential(WorkflowStepOrBuilder<S>... steps) {
        LinearWorkflowBuilder<S> builder = new LinearWorkflowBuilder<>();
        Arrays.stream(steps).sequential().forEach(builder::addStep);

        return builder;
    }

    @SafeVarargs
    public static <S extends WorkflowState<S>> ParallelWorkflow<S> parallel(WorkflowStepOrBuilder<S>... steps) {
        List<WorkflowStep<S>> workflowSteps = Arrays.stream(steps)
                .map(WorkflowStepOrBuilder::toStep)
                .toList();

        return new ParallelWorkflow<>(workflowSteps, List.of());
    }


    public static <S extends WorkflowState<S>>
    GraphWorkflowBuilder<S> builder() {
        return new GraphWorkflowBuilder<>();
    }
}
