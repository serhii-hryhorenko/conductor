# Conductor

## Brief description

**Conductor** is a simple task orchestration tool that allows you to describe your workflows in ubiquitous language
and execute them in a defined order.

It enforces immutability and clear code design of your workflows by providing a set of interfaces and classes
that you can easily reuse in other workflows/projects.

## How to get started

1. Define your `Task` classes
> `Task` is an atomic unit of work that your distributed system should have done. It rather can be a simple function or a
class with a complex logic.
When `Task` is executed it returns a `Result` which can indicate whether the execution was successful or not.

2. Define your `WorkflowState` class
> You can think it as a shared context between your steps.
It is a simple data class that should implement `copy` method to provide immutability.

3. Define your `Step` classes
> `Step` is a unit of work Workflow deals with.
It is a wrapper around `Task` that allows you to define how to react on `Task`'s `Result`.

4. Compose your `Step`s into a Workflow
> `Workflow` is a set of `Step`s that are executed in a particular order.
The order is defined by `Workflow`'s implementation.
Currently, there are two implementations available: **Linear** and **Graph**.

## Example

Graph workflow example:
```java
public class GraphWorkflowExample {
    public static void main(String[] args) {
        private static record TestState(String name, int age) implements WorkflowState<TestState> {
            @Override
            public TestState copy() {
                return new TestState(name, age);
            }
        }
        
        TestState initialState = new TestState("Amber", 34);
        
        var firstStep = WorkflowStep.<TestState, TestStateProjection, String>forTask(payload -> Result.of("Jane"))
                .thatAccepts(state -> new TestStateProjection(state.name()))
                .reducesState(TestState::setName)
                .build();
        var secondStep = WorkflowStep.<TestState, Void, Integer>forTask(unused -> Result.of(5))
                .thatAccepts(noPayload())
                .reducesState(TestState::setAge)
                .build();
        var thirdStep = WorkflowStep.<TestState, TestState, TestState>forTask(payload -> new TestState("Agnis", 16))
                .thatAccepts(workflowState())
                .reducesState((state, value) -> {
                    state.setName(value.name());
                    state.setAge(value.age());
                })
                .onSuccess(successHandler)
                .build();

        Workflow<TestState> workflow = Workflows.builder(firstStep)
                .addStep(thirdStep, thatDependsOn(firstStep, secondStep))
                .addStep(secondStep, thatDependsOn(firstStep))
                .build();
        
        TestState result = workflow.execute(initialState);
        
        // Result: TestState{name='Agnis', age=16}
    }
}
```
