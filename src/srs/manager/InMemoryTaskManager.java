package srs.manager;

import srs.model.Epic;
import srs.model.Status;
import srs.model.Subtask;
import srs.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private static int nextId = 1;

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();

    HistoryManager history = Managers.getDefaultHistory();

    @Override
    public Task createTask(String name, String description) {
        Task task = new Task(nextId++, name, description);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(String name, String description) {
        Epic epic = new Epic(nextId++, name, description);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(String name, String description, int epicId) {
        if (epics.containsKey(epicId)) {
            Subtask subtask = new Subtask(nextId++, name, description, epicId);
            subtasks.put(subtask.getId(), subtask);
            epics.get(epicId).addSubtaskId(subtask.getId());
            updateEpicStatus(epicId);
            return subtask;
        }
        return null;
    }

    @Override
    public void updateTaskStatus(int taskId, Status newStatus) {
        Task task = tasks.get(taskId);
        if (task != null) {
            task.setStatus(newStatus);
        }
    }

    @Override
    public void updateSubtaskStatus(int subtaskId, Status newStatus) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            subtask.setStatus(newStatus);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    @Override
    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        boolean allDone = true;
        boolean allNew = true;
        for (int subtaskId : subtaskIds) {
            Status status = subtasks.get(subtaskId).getStatus();
            if (status != Status.DONE) {
                allDone = false;
            }
            if (status != Status.NEW) {
                allNew = false;
            }
        }
        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        history.addToHistory(task);
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        history.addToHistory(subtask);
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        history.addToHistory(epic);
        return epic;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasksByEpic(int epicId) {
        List<Subtask> result = new ArrayList<>();
        if (epics.containsKey(epicId)) {
            for (int subtaskId : epics.get(epicId).getSubtaskIds()) {
                result.add(subtasks.get(subtaskId));
            }
        }
        return result;
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }
}