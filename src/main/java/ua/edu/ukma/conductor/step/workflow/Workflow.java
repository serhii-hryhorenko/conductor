package ua.edu.ukma.conductor.step.workflow;


import ua.edu.ukma.conductor.state.WorkflowState;
import ua.edu.ukma.conductor.step.WorkflowStep;

import java.util.List;

public abstract class Workflow<S extends WorkflowState<S>> extends WorkflowStep<S> {
    private final List<WorkflowObserver<S>> observers;

    protected Workflow(String name, List<WorkflowObserver<S>> observers) {
        super(name);
        this.observers = observers;
    }

    protected Workflow(List<WorkflowObserver<S>> observers) {
        this.observers = observers;
    }

    protected final void notifyObservers(S state) {
        observers.forEach(observer -> observer.observe(state.copy()));
    }
}
