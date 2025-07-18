package model.model;

import org.junit.jupiter.api.Test;
import srs.model.Task;
import srs.model.Type;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {
    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task(1, Type.TASK,"Task A", "Description", Duration.ofMinutes(20), LocalDateTime.of(2025, 7, 17, 22, 12));
        Task task2 = new Task(1, Type.TASK,"Task A", "Description", Duration.ofMinutes(20), LocalDateTime.of(2025, 7, 17, 22, 12));
        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны");
    }
}
