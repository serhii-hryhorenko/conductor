package ua.edu.ukma.conductor.step.workflow.graph;

import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.WorkflowStep;
import ua.edu.ukma.conductor.step.workflow.Workflow;
import ua.edu.ukma.conductor.step.workflow.WorkflowBuilder;
import ua.edu.ukma.conductor.step.workflow.WorkflowObserver;
import ua.edu.ukma.conductor.step.workflow.linear.LinearWorkflowBuilder;

import java.util.Arrays;
import java.util.function.BiConsumer;

public class GraphWorkflowBuilder<S extends WorkflowState<S>> extends WorkflowBuilder<GraphWorkflowBuilder<S>, S> {
    private final DirectedAcyclicStepGraph<S> graph;
    private WorkflowStep<S> lastAddedStep;

    private boolean synchronous = false;

    public GraphWorkflowBuilder(WorkflowStep<S> initialStep) {
        this.graph = new DirectedAcyclicStepGraph<>(initialStep);
    }

    public GraphWorkflowBuilder<S> addStep(WorkflowStep<S> step,
                                           BiConsumer<GraphWorkflowBuilder<S>, WorkflowStep<S>> edgeCreator) {
        edgeCreator.accept(this, step);
        lastAddedStep = step;

        return this;
    }

    public GraphWorkflowBuilder<S> synchronous() {
        synchronous = true;
        return this;
    }

    @Override
    public GraphWorkflowBuilder<S> addStep(WorkflowStep<S> step) {
        return addStep(step, dependsOnLastStep());
    }

    @SafeVarargs
    public static <S extends WorkflowState<S>>
    BiConsumer<GraphWorkflowBuilder<S>, WorkflowStep<S>> dependsOn(WorkflowStep<S>... steps) {
        return (builder, to) -> Arrays.stream(steps).sequential().forEach(step -> builder.graph.addEdge(step, to));
    }

    public static <S extends WorkflowState<S>>
    BiConsumer<GraphWorkflowBuilder<S>, WorkflowStep<S>> dependsOnLastStep() {
        return (builder, to) -> builder.graph.addEdge(builder.lastAddedStep, to);
    }

    public Workflow<S> build() {
        if (synchronous) {
            LinearWorkflowBuilder<S> builder = new LinearWorkflowBuilder<>();

            graph.topologicalSort().forEach(builder::addStep);
            builder.attachObservers(observers().toArray(WorkflowObserver[]::new));

            return builder.build();
        }

        return new GraphWorkflow<>(graph, observers());
    }
}
