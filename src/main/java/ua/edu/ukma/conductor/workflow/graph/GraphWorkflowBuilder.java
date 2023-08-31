package ua.edu.ukma.conductor.workflow.graph;

import ua.edu.ukma.conductor.workflow.Workflow;
import ua.edu.ukma.conductor.workflow.WorkflowBuilder;
import ua.edu.ukma.conductor.workflow.WorkflowState;
import ua.edu.ukma.conductor.workflow.linear.LinearWorkflow;
import ua.edu.ukma.conductor.workflow.step.Step;

import java.util.Arrays;
import java.util.function.BiConsumer;

public class GraphWorkflowBuilder<S extends WorkflowState<S>> extends WorkflowBuilder<GraphWorkflowBuilder<S>, S> {
    private final DirectedAcyclicStepGraph<S> graph;

    public GraphWorkflowBuilder(Step<?, S, ?> initialStep) {
        this.graph = new DirectedAcyclicStepGraph<>(initialStep);
    }

    public GraphWorkflowBuilder<S> addStep(Step<?, S, ?> step, BiConsumer<GraphWorkflowBuilder<S>, Step<?, S, ?>> edgeCreator) {
        edgeCreator.accept(this, step);
        return this;
    }

    @SafeVarargs
    public static <S extends WorkflowState<S>> BiConsumer<GraphWorkflowBuilder<S>, Step<?, S, ?>> thatDependsOn(Step<?, S, ?>... steps) {
        return (builder, to) -> {
            builder.graph.addVertex(to);
            Arrays.stream(steps).sequential().forEach(step -> builder.graph.addEdge(step, to));
        };
    }

    public Workflow<S> build() {
        return new LinearWorkflow<>(graph.topologicalSort(), observers());
    }
}
