package model.model;

import org.junit.jupiter.api.Test;
import srs.model.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {
    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task(1, "Task A", "Description");
        Task task2 = new Task(1, "Task A", "Description");
        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны");
    }
}
