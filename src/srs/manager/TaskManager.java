package srs.manager;

import srs.model.Epic;
import srs.model.Status;
import srs.model.Subtask;
import srs.model.Task;
import java.util.List;

public interface TaskManager {

    Task createTask(String name, String description);
    Epic createEpic(String name, String description);
    Subtask createSubtask(String name, String description, int epicId);

    void updateTaskStatus(int taskId, Status newStatus);

    void updateSubtaskStatus(int subtaskId, Status newStatus);

    void updateEpicStatus(int epicId);

    void deleteTask(int id);

    void deleteEpic(int id);

    List<Task> getHistory();

    Task getTask(int id);
    Subtask getSubtask(int id);
    Epic getEpic(int id);

    List<Task> getAllTasks();
    List<Epic> getAllEpics();
    List<Subtask> getSubtasksByEpic(int epicId);
}
