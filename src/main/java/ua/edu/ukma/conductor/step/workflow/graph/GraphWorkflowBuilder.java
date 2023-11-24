package ua.edu.ukma.conductor.step.workflow.graph;

import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.Step;
import ua.edu.ukma.conductor.step.workflow.Workflow;
import ua.edu.ukma.conductor.step.workflow.WorkflowBuilder;
import ua.edu.ukma.conductor.step.workflow.linear.LinearWorkflow;

import java.util.Arrays;
import java.util.function.BiConsumer;

public class GraphWorkflowBuilder<S extends WorkflowState<S>> extends WorkflowBuilder<GraphWorkflowBuilder<S>, S> {
    private final DirectedAcyclicStepGraph<S> graph;
    private Step<S> lastAddedStep;

    public GraphWorkflowBuilder(Step<S> initialStep) {
        this.graph = new DirectedAcyclicStepGraph<>(initialStep);
    }

    public GraphWorkflowBuilder<S> addStep(Step<S> step,
                                           BiConsumer<GraphWorkflowBuilder<S>, Step<S>> edgeCreator) {
        edgeCreator.accept(this, step);
        lastAddedStep = step;

        return this;
    }

    @Override
    public GraphWorkflowBuilder<S> addStep(Step<S> step) {
        return addStep(step, thatDependsOnLastStep());
    }

    @SafeVarargs
    public static <S extends WorkflowState<S>> BiConsumer<GraphWorkflowBuilder<S>, Step<S>> thatDependsOn(Step<S>... steps) {
        return (builder, to) -> Arrays.stream(steps).sequential().forEach(step -> builder.graph.addEdge(step, to));
    }

    public static <S extends WorkflowState<S>> BiConsumer<GraphWorkflowBuilder<S>, Step<S>> thatDependsOnLastStep() {
        return (builder, to) -> builder.graph.addEdge(builder.lastAddedStep, to);
    }

    public Workflow<S> build() {
        return new LinearWorkflow<>(graph.topologicalSort(), observers());
    }
}
