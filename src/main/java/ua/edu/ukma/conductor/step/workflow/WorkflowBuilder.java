package ua.edu.ukma.conductor.step.workflow;

import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.StepBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class WorkflowBuilder<B extends WorkflowBuilder<B, S>,
        S extends WorkflowState<S>> extends StepBuilder<B, Workflow<S>, S> {
    private final List<WorkflowObserver<S>> observers = new ArrayList<>();

    private String name;

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final B attachObservers(WorkflowObserver<S>... observers) {
        this.observers.addAll(Arrays.stream(observers).toList());
        return (B) this;
    }

    public B named(String name) {
        this.name = name;
        return (B) this;
    }

    protected List<WorkflowObserver<S>> observers() {
        return observers;
    }

    protected String name() {
        return name;
    }
}
