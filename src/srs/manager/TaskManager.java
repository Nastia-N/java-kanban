package srs.manager;

import srs.model.Epic;
import srs.model.Status;
import srs.model.Subtask;
import srs.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface TaskManager {

    Task createTask(String name, String description, Duration duration, LocalDateTime startTime);

    Epic createEpic(String name, String description);

    Subtask createSubtask(String name, String description, int epicId, Duration duration, LocalDateTime startTime);

    void updateTaskStatus(int taskId, Status newStatus);

    void updateSubtaskStatus(int subtaskId, Status newStatus);

    void updateEpicStatus(int epicId);

    void deleteTask(int id);

    void deleteTasks();

    void deleteSubtask(int id);

    void deleteSubtasks();

    void deleteEpic(int id);

    void deleteEpics();

    List<Task> getHistory();

    Task getTask(int id);

    Subtask getSubtask(int id);

    Epic getEpic(int id);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getSubtasksByEpic(int epicId);

    Set<Task> getPrioritizedTasks();
}
