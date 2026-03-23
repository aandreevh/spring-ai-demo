package demo.ai.spring.structuredOutput2.task;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

//TODO: Explain DTO mappings
public record TaskClassificationDto(
        @JsonPropertyDescription("Describes the priority of a task. LOW - when free time available, HIGH - do it soonest possible")
        TaskPriority priority,
        @JsonPropertyDescription("Describes estimated effort for task completion in hours")
        double effort,
        @JsonPropertyDescription("Gives explanation of why task is urgent and why the described effort is chosen")
        String reason) {
}
