package srs.manager;

import srs.model.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static java.nio.file.Files.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public Task createTask(String name, String description, Duration duration, LocalDateTime startTime) {
        Task task = super.createTask(name, description, duration, startTime);
        save();
        return task;
    }

    @Override
    public Epic createEpic(String name, String description) {
        Epic epic = super.createEpic(name, description);
        save();
        return epic;
    }

    @Override
    public Subtask createSubtask(String name, String description, int epicId, Duration duration, LocalDateTime startTime) {
        Subtask subtask = super.createSubtask(name, description, epicId, duration, startTime);
        save();
        return subtask;
    }

    @Override
    public void updateTaskStatus(int taskId, Status newStatus) {
        super.updateTaskStatus(taskId, newStatus);
        save();
    }

    @Override
    public void updateSubtaskStatus(int subtaskId, Status newStatus) {
        super.updateSubtaskStatus(subtaskId, newStatus);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    private Task fromString(String value) {
        String [] split = value.split(",");
        Task result = switch (Type.valueOf(split[1])) {
            case TASK ->
                    new Task(Integer.parseInt(split[0]), Type.valueOf(split[1]), split[2], split[3], Status.valueOf(split[4]), Duration.ofMinutes(Long.parseLong(split[5])), LocalDateTime.parse(split[6]));
            case EPIC ->
                    new Epic(Integer.parseInt(split[0]), split[2], split[3], Status.valueOf(split[4]), Duration.ofMinutes(Long.parseLong(split[5])), LocalDateTime.parse(split[6]));
            case SUBTASK ->
                    new Subtask(Integer.parseInt(split[0]), split[2], split[3], Status.valueOf(split[4]), Integer.parseInt(split[7]), Duration.ofMinutes(Long.parseLong(split[5])), LocalDateTime.parse(split[6]));
            default -> {
                System.out.println("Такой формат не предусмотрен");
                yield null;
            }
        };

        return result;
    }

    private void save() {
        try {
            if (!exists(file.toPath())) {
                createFile(file.toPath());
            }

            Writer fileWriter = new FileWriter(file.getAbsolutePath());

            fileWriter.write("id,type,name,status,description,duration,startTime,epic\n");
            for (Task task : getAllTasks()) {
                fileWriter.write(task.toCSVString());
                fileWriter.write("\n");
            }
            for (Epic epic : getAllEpics()) {
                fileWriter.write(epic.toCSVString());
                fileWriter.write("\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                fileWriter.write(subtask.toCSVString());
                fileWriter.write("\n");
                }

            fileWriter.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        int maxId = 0;

        try {
            List<String> lines = readAllLines(file.toPath());

            for (int i = 1; i < lines.size(); i++) {
                if (!lines.get(i).isBlank()) {
                    Task task = manager.fromString(lines.get(i));
                    if (task == null) {
                        continue;
                    }

                    switch (task.getType()) {
                        case TASK:
                            manager.putTask(task);
                            break;
                        case EPIC:
                            manager.putEpic((Epic) task);
                            break;
                        case SUBTASK:
                            manager.putSubtask((Subtask) task);
                        default:
                            System.out.println("Такой формат не предусмотрен");
                    }

                    if (task.getId() > maxId) {
                        maxId = task.getId();
                    }
                }
            }
            manager.updateNextId(maxId + 1);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        }
        return manager;
    }
}