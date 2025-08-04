package srs;

import srs.manager.Managers;
import srs.manager.TaskManager;
import srs.model.Epic;
import srs.model.Status;
import srs.model.Subtask;
import srs.model.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main2 {
    public static void main2(String[] args) {
        TaskManager manager = Managers.getFileBackedTM(new File("TaskData.csv"));

        Task task1 = manager.createTask("Помыть посуду", "Помыть всю посуду вечером", Duration.ofMinutes(20), LocalDateTime.of(2025, 7, 17, 22, 12));
        manager.createTask("Сделать уроки", "Математика и русский язык", Duration.ofMinutes(40), LocalDateTime.of(2025, 7, 28, 16, 12));

        Epic epic1 = manager.createEpic("Переезд", "Организовать переезд в новый офис");
        Subtask epic1Subtask1 = manager.createSubtask("Упаковать книги", "Упаковать все книги в коробки", epic1.getId(), Duration.ofMinutes(20), LocalDateTime.of(2025, 7, 16, 15, 12));
        Subtask epic1Subtask2 = manager.createSubtask("Нанять грузчиков", "Найти грузчиков на 10 утра", epic1.getId(), Duration.ofMinutes(20), LocalDateTime.of(2025, 7, 17, 8, 5));

        Epic epic2 = manager.createEpic("Ремонт", "Сделать ремонт в квартире");
        manager.createSubtask("Купить краску", "Выбрать цвет и купить 5 банок", epic2.getId(), Duration.ofMinutes(30), LocalDateTime.of(2025, 7, 19, 12, 12));

        manager.getTask(1);
        manager.getTask(1);
        manager.getSubtask(5);
        manager.getEpic(3);
        manager.getEpic(3);
        manager.getSubtask(4);
        manager.getSubtask(5);
        manager.getTask(1);
        manager.getSubtask(5);
        manager.getEpic(3);
        manager.getEpic(3);


        System.out.println(" ");
        System.out.println("======После добавления======");
        System.out.println(" ");
        printAllTasks(manager);
        System.out.println(" ");
        printHistory(manager);
        System.out.println(" ");

        System.out.println("======После изменения статусов======");
        System.out.println(" ");
        manager.updateSubtaskStatus(epic1Subtask1.getId(), Status.IN_PROGRESS);
        manager.updateSubtaskStatus(epic1Subtask2.getId(), Status.DONE);
        manager.updateTaskStatus(task1.getId(), Status.DONE);
        printAllTasks(manager);
        System.out.println(" ");

        System.out.println("======После удаления======");
        System.out.println(" ");
        manager.deleteTask(task1.getId());
        manager.deleteEpic(epic2.getId());
        printAllTasks(manager);
        System.out.println(" ");
        printHistory(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("##Обычные задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task.getName() + ", описание: " + task.getDescription() + ", статус: " + task.getStatus() + ", id: " + task.getId());
            System.out.println(" ");
        }

        System.out.println("##Эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(" ");
            System.out.println("- " + epic.getName() + ", описание: " + epic.getDescription() + ", статус: " + epic.getStatus() + ", id: " + epic.getId());

            System.out.println("Подзадачи:");
            for (Subtask subtask : manager.getSubtasksByEpic(epic.getId())) {
                System.out.println(subtask.getName() + ", описание: " + subtask.getDescription() + ", статус: " + subtask.getStatus() + ", id: " + subtask.getId());
            }
        }
        System.out.println(" ");
        printPrioritizedTasks(manager);
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("====== История ======");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

    private static void printPrioritizedTasks(TaskManager manager) {
        System.out.println("====== Приоритет ======");
        for (Task task : manager.getPrioritizedTasks()) {
            System.out.println(task);
        }
    }
}