package handler;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import srs.handler.HttpTaskServer;
import srs.manager.InMemoryTaskManager;
import srs.manager.TaskManager;
import srs.model.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {

    TaskManager manager = new InMemoryTaskManager();
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerTasksTest() {
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
        HttpTaskServer.start(manager);
    }

    @AfterEach
    public void shutDown() {
        HttpTaskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task(1, Type.TASK, "Test Task", "Testing task 2", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный статус код");

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test Task", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = manager.createEpic("Test Epic", "Description");

        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный статус код");
        assertTrue(manager.getAllEpics().isEmpty(), "Список эпиков должен быть пустым");
    }

    @Test
    public void testGetSubtask() throws IOException, InterruptedException {
        Epic epic = manager.createEpic("Test Epic", "Description");
        manager.createSubtask("Test Subtask", "Description", epic.getId(), Duration.ofMinutes(5), LocalDateTime.of(2025, 7, 17, 22, 12));

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный статус код");
        List<Subtask> subtasksFromEpic = manager.getSubtasksByEpic(epic.getId());
        assertFalse(subtasksFromEpic.isEmpty(), "Список подзадач эпика не должен быть пустым");
    }
}