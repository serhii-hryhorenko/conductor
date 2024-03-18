package ua.edu.ukma.conductor.step.workflow.graph;

import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.WorkflowStep;
import ua.edu.ukma.conductor.step.WorkflowStepOrBuilder;
import ua.edu.ukma.conductor.step.workflow.Workflow;
import ua.edu.ukma.conductor.step.workflow.WorkflowBuilder;
import ua.edu.ukma.conductor.step.workflow.WorkflowObserver;
import ua.edu.ukma.conductor.step.workflow.linear.LinearWorkflowBuilder;

import java.util.Arrays;
import java.util.function.BiConsumer;

public final class GraphWorkflowBuilder<S extends WorkflowState<S>> extends WorkflowBuilder<GraphWorkflowBuilder<S>, S> {
    private final DependencyDAG<S> graph;
    private WorkflowStep<S> lastAddedStep;

    private boolean synchronous = false;

    public GraphWorkflowBuilder() {
        this.graph = new DependencyDAG<>();
    }

    @Override
    public GraphWorkflowBuilder<S> addStep(WorkflowStep<S> step) {
        return addStep(step, dependsOnLastStep());
    }

    @Override
    public GraphWorkflowBuilder<S> addStep(WorkflowStepOrBuilder<S> stepOrBuilder) {
        return addStep(stepOrBuilder, dependsOnLastStep());
    }

    public GraphWorkflowBuilder<S> addStep(WorkflowStepOrBuilder<S> stepOrBuilder,
                                           BiConsumer<GraphWorkflowBuilder<S>, WorkflowStep<S>> edgeCreator) {
        WorkflowStep<S> step = stepOrBuilder.toStep();

        edgeCreator.accept(this, step);
        lastAddedStep = step;

        return this;
    }

    public GraphWorkflowBuilder<S> synchronous() {
        synchronous = true;
        return this;
    }

    @SafeVarargs
    public static <S extends WorkflowState<S>>
    BiConsumer<GraphWorkflowBuilder<S>, WorkflowStep<S>> dependsOn(WorkflowStepOrBuilder<S>... steps) {
        return (builder, to) -> Arrays.stream(steps).sequential()
                .forEach(step -> builder.graph.addEdge(step.toStep(), to));
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

            return builder.named(name()).build();
        }

        return new GraphWorkflow<>(name(), graph, observers());
    }
}
