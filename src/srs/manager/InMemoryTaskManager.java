package srs.manager;

import srs.handler.NotFoundException;
import srs.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private static int nextId = 1;

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager history = Managers.getDefaultHistory();

    public void updateStartTimeEpic(Integer epicId) {
        LocalDateTime startTime = null;
        List<Subtask> subtasks = getSubtasksByEpic(epicId);
        for (Subtask sub : subtasks) {
            if (startTime == null || sub.getStartTime().isBefore(startTime)) {
                startTime = sub.getStartTime();
            }
        }
        epics.get(epicId).setStartTime(startTime);
    }

    public void updateEndTimeEpic(Integer epicId) {
        LocalDateTime endTime = null;
        List<Subtask> subtasks = getSubtasksByEpic(epicId);
        for (Subtask sub : subtasks) {
            if (endTime == null || sub.getEndTime().isAfter(endTime)) {
                endTime = sub.getEndTime();
            }
        }
        epics.get(epicId).setEndTime(endTime);
    }

    public Set<Task> getPrioritizedTasks() {
        Comparator<Task> comparator = new Comparator<Task>() {

            @Override
            public int compare(Task o1, Task o2) {
                return o1.getStartTime().compareTo(o2.getStartTime());
            }
        };

        Set<Task> tree = new TreeSet<>(comparator);
        tree.addAll(getAllTasks());
        tree.addAll(getAllEpics());
        tree.addAll(getAllSubtasks());
        return tree;
    }

    public boolean hasTimeConflict(Task t1, Task t2) {
        return !t1.getEndTime().isBefore(t2.getStartTime()) &&
                !t1.getStartTime().isAfter(t2.getEndTime());
    }

    public void chekTimeConflict(Task newTask) {
        boolean hasConflict = getPrioritizedTasks().stream()
                .anyMatch(task -> hasTimeConflict(newTask, task));
        if (hasConflict) {
            throw new TimeConflictException("В это время уже запланирована задача.");
        }
    }

    @Override
    public Task createTask(String name, String description, Duration duration, LocalDateTime startTime) {
        Task task = new Task(nextId++, Type.TASK, name, description, duration, startTime);
        chekTimeConflict(task);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(String name, String description) {
        Epic epic = new Epic(nextId++, name, description);
        chekTimeConflict(epic);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(String name, String description, int epicId, Duration duration, LocalDateTime startTime) {
        if (epics.containsKey(epicId)) {
            Subtask subtask = new Subtask(nextId++, name, description, epicId, duration, startTime);
            chekTimeConflict(subtask);
            subtasks.put(subtask.getId(), subtask);
            Epic e = epics.get(epicId);
            e.addSubtaskId(subtask.getId());
            updateEpicStatus(epicId);
            updateStartTimeEpic(epicId);
            updateEndTimeEpic(epicId);
            e.setDuration(Duration.between(e.getStartTime(), e.getEndTime()));
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
        history.remove(id);
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteSubtask(int id) {
        subtasks.remove(id);
        history.remove(id);
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            epic.getSubtaskIds()
                    .forEach(subtasks::remove);
            epics.remove(id);
            history.remove(id);
        }
    }

    @Override
    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Неверный id");
        }
        history.add(task);
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException("Неверный id");
        }
        history.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Неверный id");
        }
        history.add(epic);
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

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getSubtasksByEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            return epics.get(epicId).getSubtaskIds().stream()
                    .map(subtasks::get)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    public void putTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void putEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void putSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    public void updateNextId(int newId) {
        nextId = newId;
    }
}