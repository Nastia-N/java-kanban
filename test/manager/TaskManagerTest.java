package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import srs.manager.InMemoryTaskManager;
import srs.manager.TaskManager;
import srs.model.Epic;
import srs.model.Status;
import srs.model.Subtask;
import srs.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {
    private TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void managerShouldAddAndFindDifferentTaskTypes() {
        Task task = manager.createTask("Помыть посуду", "Помыть всю посуду вечером");
        assertNotNull(task, "Задача не создана");
        assertNotNull(manager.getTask(task.getId()), "Задача по id не найдена");

        Epic epic = manager.createEpic("Переезд", "Организовать переезд в новый офис");
        assertNotNull(epic, "Epic не создан");
        assertNotNull(manager.getEpic(epic.getId()), "Epic по id не найден");

        Subtask subtask = manager.createSubtask("Упаковать книги", "Упаковать все книги в коробки", epic.getId());
        assertNotNull(subtask, "Подзадача не создана");
        assertNotNull(manager.getSubtask(subtask.getId()), "Подзадача по id не найдена");
    }

    @Test
    void taskShouldRemainUnchangedWhenAddedToManager() {
        Task originalTask = manager.createTask("Помыть посуду", "Помыть всю посуду вечером");
        Task addedTask = manager.getTask(originalTask.getId());
        assertEquals(originalTask.getName(), addedTask.getName(), "Имя изменилось");
        assertEquals(originalTask.getDescription(), addedTask.getDescription(), "Описание изменилось");
        assertEquals(originalTask.getStatus(), addedTask.getStatus(), "Статус изменился");
    }

    @Test
    void shouldRemoveTaskById() {
        Task task = manager.createTask("Помыть посуду", "Помыть всю посуду вечером");
        assertNotNull(task, "Задача не создана");
        assertNotNull(manager.getTask(task.getId()), "Задача по id не найдена");
        manager.deleteTask(task.getId());
        assertNull(manager.getTask(task.getId()), "Задача не удалена");

        Epic epic = manager.createEpic("Переезд", "Организовать переезд в новый офис");
        assertNotNull(epic, "Epic не создан");
        assertNotNull(manager.getEpic(epic.getId()), "Epic по id не найден");
        Subtask subtask = manager.createSubtask("Упаковать книги", "Упаковать все книги в коробки", epic.getId());
        assertNotNull(subtask, "Подзадача не создана");
        manager.deleteEpic(epic.getId());
        assertNull(manager.getEpic(epic.getId()), "Задача не удалена");
        assertNull(manager.getSubtask(subtask.getId()), "Задача не удалена");
    }

    @Test
    void shouldUpdateTaskStatus() {
        Task task = manager.createTask("Task", "Description");
        manager.updateTaskStatus(task.getId(), Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, manager.getTask(task.getId()).getStatus(), "Статус задачи должен обновиться");

        Epic epic = manager.createEpic("Epic", "Description");
        Subtask subtask1 = manager.createSubtask("Subtask 1", "Desc", epic.getId());
        Subtask subtask2 = manager.createSubtask("Subtask 2", "Desc", epic.getId());
        manager.updateSubtaskStatus(subtask1.getId(), Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Эпик должен быть IN_PROGRESS, если хотя бы одна подзадача в этом статусе");
        manager.updateSubtaskStatus(subtask2.getId(), Status.DONE);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Эпик остается IN_PROGRESS, если есть задачи в разных статусах");
        manager.updateSubtaskStatus(subtask1.getId(), Status.DONE);
        assertEquals(Status.DONE, epic.getStatus(), "Эпик должен быть DONE, когда все подзадачи завершены");
    }

    @Test
    void createSubtaskShouldReturnNullWhenEpicNotExists() {
        String name = "Test Subtask";
        String description = "Test Description";
        int nonExistentEpicId = 1000;
        Subtask result = manager.createSubtask(name, description, nonExistentEpicId);
        assertNull(result, "Метод должен возвращать null при попытке создать подзадачу для несуществующего эпика");
    }

    @Test
    void getAllTasksShouldReturnAllCreatedEpics() {
        Task task1 = manager.createTask("Task 1", "Description");
        Task task2 = manager.createTask("Task 2", "Description");
        List<Task> result = manager.getAllTasks();

        assertEquals(2, result.size(), "Должны вернуться все созданные задачи");
        assertTrue(result.contains(task1), "Список должен содержать первую задачу");
        assertTrue(result.contains(task2), "Список должен содержать вторую задачу");
    }

    @Test
    void getAllEpicsShouldReturnAllCreatedEpics() {
        Epic epic1 = manager.createEpic("Epic 1", "Description");
        Epic epic2 = manager.createEpic("Epic 2", "Description");
        List<Epic> result = manager.getAllEpics();

        assertEquals(2, result.size(), "Должны вернуться все созданные эпики");
        assertTrue(result.contains(epic1), "Список должен содержать первый эпик");
        assertTrue(result.contains(epic2), "Список должен содержать второй эпик");
    }

    @Test
    void getSubtasksByEpicShouldReturnAllSubtasksForEpic() {
        Epic epic = manager.createEpic("Epic", "Description");
        Subtask subtask1 = manager.createSubtask("Subtask 1", "Description", epic.getId());
        Subtask subtask2 = manager.createSubtask("Subtask 2", "Description", epic.getId());
        List<Subtask> result = manager.getSubtasksByEpic(epic.getId());

        assertEquals(2, result.size(), "Должны вернуться все подзадачи эпика");
        assertTrue(result.contains(subtask1), "Список должен содержать первую подзадачу");
        assertTrue(result.contains(subtask2), "Список должен содержать вторую подзадачу");
    }

    @Test
    void getSubtasksByEpicShouldReturnEmptyListForNonExistentEpic() {
        List<Subtask> result = manager.getSubtasksByEpic(999); // Несуществующий ID
        assertNotNull(result, "Метод не должен возвращать null");
        assertTrue(result.isEmpty(), "Для несуществующего эпика должен вернуться пустой список");
    }


}
