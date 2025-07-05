package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import srs.manager.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;

import static java.io.File.createTempFile;
import static java.nio.file.Files.readString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static srs.manager.FileBackedTaskManager.loadFromFile;

public class FileBackedTaskManagerTest {
    private File testFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        testFile = createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(testFile);
    }

    @Test
    void tasksLoadFromFile() {
        manager.createTask("Помыть посуду", "Помыть всю посуду вечером");
        manager.createEpic("Переезд", "Организовать переезд в новый офис");
        FileBackedTaskManager manager1 = loadFromFile(testFile);
        assertEquals(1, manager1.getAllTasks().size(), "Количество Task не совпадает");
        assertEquals(1, manager1.getAllEpics().size(), "Количество Epic не совпадает");
        assertEquals("Task{id=1, name='Помыть посуду', description='Помыть всю посуду вечером', status=NEW}", manager1.getTask(1).toString(), "Задача не совпадает");
        assertEquals("Task{id=2, name='Переезд', description='Организовать переезд в новый офис', status=NEW}", manager1.getEpic(2).toString(), "Задача не совпадает");
    }
}
