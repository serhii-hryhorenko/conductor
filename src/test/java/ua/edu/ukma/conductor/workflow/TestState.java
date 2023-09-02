package ua.edu.ukma.conductor.workflow;

import ua.edu.ukma.conductor.task.PayloadType;
import ua.edu.ukma.conductor.task.ResultType;

public class TestState extends WorkflowState<TestState> implements PayloadType, ResultType {
    private String name;
    private int age;

    public TestState(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    protected TestState copy() {
        return new TestState(name, age);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String name() {
        return name;
    }

    public int age() {
        return age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestState testState)) return false;

        if (age != testState.age) return false;
        return name.equals(testState.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + age;
        return result;
    }

    @Override
    public String toString() {
        return "TestState{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
