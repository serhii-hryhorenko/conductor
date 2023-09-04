package ua.edu.ukma.conductor.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class WorkflowBuilder<B extends WorkflowBuilder<B, S>,
        S extends WorkflowState<S>> extends StepBuilder<B, Workflow<S>, S> {
    private final List<WorkflowObserver<S>> observers = new ArrayList<>();

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final B attachObservers(WorkflowObserver<S>... observers) {
        this.observers.addAll(Arrays.stream(observers).toList());
        return (B) this;
    }

    protected List<WorkflowObserver<S>> observers() {
        return observers;
    }
}
