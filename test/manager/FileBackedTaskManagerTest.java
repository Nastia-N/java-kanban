package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import srs.manager.FileBackedTaskManager;
import srs.model.Epic;
import srs.model.Subtask;
import srs.model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File testFile;

    @BeforeEach
    @Override
    void setUp() throws IOException {
        testFile = Files.createTempFile("tasks", ".csv").toFile();
        manager = createTaskManager(); // Явная инициализация менеджера
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(testFile);
    }

    @Test
    void shouldSaveAndLoadTimeParameters() throws IOException {
        LocalDateTime startTime1 = LocalDateTime.of(2025, 6, 1, 10, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2025, 7, 1, 10, 0);
        Duration duration = Duration.ofMinutes(30);

        Task task = manager.createTask("Task", "Desc", duration, startTime1);
        Epic epic = manager.createEpic("Epic", "Desc");
        Subtask subtask = manager.createSubtask("Subtask", "Desc",
                epic.getId(), Duration.ofHours(1), startTime2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);

        assertAll(
                () -> assertNotNull(loadedManager.getTask(task.getId()), "Задача не загрузилась"),
                () -> assertEquals(startTime1, loadedManager.getTask(task.getId()).getStartTime(),
                        "Время начала не совпадает"),
                () -> assertEquals(duration, loadedManager.getTask(task.getId()).getDuration(),
                        "Продолжительность не совпадает"),
                () -> assertNotNull(loadedManager.getEpic(epic.getId()), "Эпик не загрузился"),
                () -> assertEquals(startTime2, loadedManager.getEpic(epic.getId()).getStartTime(),
                        "Время начала эпика не совпадает")
        );
    }
}