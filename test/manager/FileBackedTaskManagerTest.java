package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import srs.manager.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static java.io.File.createTempFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static srs.manager.FileBackedTaskManager.loadFromFile;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File testFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        testFile = createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(testFile);
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(testFile);
    }

    @Test
    void tasksLoadFromFile() {
        manager.createTask("Помыть посуду", "Помыть всю посуду вечером", Duration.ofMinutes(20), LocalDateTime.of(2025, 7, 17, 20, 12));
        manager.createEpic("Переезд", "Организовать переезд в новый офис");
        FileBackedTaskManager manager1 = loadFromFile(testFile);
        assertEquals(1, manager1.getAllTasks().size(), "Количество Task не совпадает");
        assertEquals(1, manager1.getAllEpics().size(), "Количество Epic не совпадает");
        assertEquals("Task{id=1, type=TASK, name='Помыть посуду', description='Помыть всю посуду вечером', status=NEW, duration=PT20M, startTime=2025-07-17T20:12}", manager1.getTask(1).toString(), "Задача не совпадает");
        assertEquals("Task{id=2, type=EPIC, name='Переезд', description='Организовать переезд в новый офис', status=NEW, duration=PT1M, startTime=2025-07-18T11:11}", manager1.getEpic(2).toString(), "Задача не совпадает");
    }
}
