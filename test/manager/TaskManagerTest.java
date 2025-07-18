package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import srs.manager.TaskManager;
import srs.model.Status;
import srs.model.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() throws IOException {
        manager = createTaskManager();
    }

    @Test
    void shouldMaintainTimeAfterStatusUpdate() {
        assertNotNull(manager, "Менеджер задач не должен быть null");

        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(45);

        Task task = manager.createTask("Task", "Desc", duration, startTime);
        assertNotNull(task, "Задача не должна быть null");

        manager.updateTaskStatus(task.getId(), Status.IN_PROGRESS);

        Task updated = manager.getTask(task.getId());
        assertNotNull(updated, "Обновленная задача не должна быть null");

        assertAll(
                () -> assertEquals(Status.IN_PROGRESS, updated.getStatus(), "Статус должен обновиться"),
                () -> assertEquals(startTime, updated.getStartTime(), "Время начала не должно измениться"),
                () -> assertEquals(duration, updated.getDuration(), "Продолжительность не должна измениться")
        );
    }


    @Test
    void shouldCorrectlyHandleTaskTimeParameters() {
        LocalDateTime startTime = LocalDateTime.of(2023, 6, 1, 10, 0);
        Duration duration = Duration.ofMinutes(30);

        Task task = manager.createTask("Task with time", "Description", duration, startTime);

        assertAll(
                () -> assertEquals(startTime, task.getStartTime(), "Время начала не совпадает"),
                () -> assertEquals(duration, task.getDuration(), "Продолжительность не совпадает"),
                () -> assertEquals(startTime.plus(duration), task.getEndTime(), "Время окончания рассчитано неверно")
        );
    }
}
