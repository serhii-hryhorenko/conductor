package ua.edu.ukma.conductor.workflow;

import ua.edu.ukma.conductor.task.PayloadType;

public record TestStateProjection(String name) implements PayloadType {
}
