package test;

import org.junit.jupiter.api.Test;
import srs.manager.Managers;
import srs.manager.TaskManager;
import srs.model.Epic;
import srs.model.Status;
import srs.model.Subtask;
import srs.model.Task;
import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {

    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task(1, "Task A", "Description");
        Task task2 = new Task(1, "Task A", "Description");
        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны");
    }

    @Test
    void subtasksWithSameIdShouldBeEqual() {
        Subtask subtask1 = new Subtask(1, "Sub A", "Desc", 6);
        Subtask subtask2 = new Subtask(1, "Sub A", "Desc", 6);
        assertEquals(subtask1, subtask2, "Подзадачи с одинаковым id должны быть равны");
    }

    @Test
    void epicCannotBeSubtaskOfItself() {
        Epic epic = new Epic(5, "Epic", "Desc");
        epic.addSubtaskId(epic.getId());
        assertTrue(epic.getSubtaskIds().isEmpty(), "Epic нельзя добавить в самого себя в виде подзадачи");
    }

    @Test
    void managersShouldReturnInitializedInstances() {
        assertNotNull(Managers.getDefault(), "Менеджер задач не должен быть null");
        assertNotNull(Managers.getDefaultHistory(), "Менеджер истории не должен быть null");
    }

    @Test
    void managerShouldAddAndFindDifferentTaskTypes() {
        TaskManager manager = Managers.getDefault();

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
        TaskManager manager = Managers.getDefault();
        Task originalTask = manager.createTask("Помыть посуду", "Помыть всю посуду вечером");
        Task addedTask = manager.getTask(originalTask.getId());
        assertEquals(originalTask.getName(), addedTask.getName(), "Имя изменилось");
        assertEquals(originalTask.getDescription(), addedTask.getDescription(), "Описание изменилось");
        assertEquals(originalTask.getStatus(), addedTask.getStatus(), "Статус изменился");
    }

    @Test
    void shouldRemoveTaskById() {
        TaskManager manager = Managers.getDefault();

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
        TaskManager manager = Managers.getDefault();
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
}
