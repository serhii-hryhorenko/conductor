package ua.edu.ukma.conductor.workflow;

import ua.edu.ukma.conductor.task.PayloadType;
import ua.edu.ukma.conductor.task.ResultType;
import ua.edu.ukma.conductor.workflow.step.Step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class WorkflowBuilder<B extends WorkflowBuilder<B, S>, S extends WorkflowState<S>> {
    private final List<WorkflowObserver<S>> observers = new ArrayList<>();

    public abstract B addStep(Step<? extends ResultType, S, ? extends PayloadType> step);

    public abstract Workflow<S> build();

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
