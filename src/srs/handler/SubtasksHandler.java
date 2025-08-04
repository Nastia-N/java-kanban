package srs.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import srs.manager.TaskManager;
import srs.manager.TimeConflictException;
import srs.model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    public SubtasksHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "";
        try {
            String method = httpExchange.getRequestMethod();
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Task newBody = gson.fromJson(body, Task.class);

            URI requestURI = httpExchange.getRequestURI();
            String path = requestURI.getPath();
            String[] splitStrings = path.split("/");
            int code = 0;

            switch (method) {
                case "POST":
                    response = gson.toJson(manager.createSubtask(newBody.getName(), newBody.getDescription(), newBody.getId(), newBody.getDuration(), newBody.getStartTime()));
                    code = 201;
                    break;
                case "GET":
                    if (splitStrings.length == 3) {
                        String str = splitStrings[2];
                        int id = Integer.parseInt(str);
                        response = gson.toJson(manager.getSubtask(id));
                    } else {
                        response = "Такого запроса не существует";
                    }
                    code = 200;
                    break;
                case "DELETE":
                    String str = splitStrings[2];
                    int id = Integer.parseInt(str);
                    manager.deleteSubtask(id);
                    code = 200;
                    break;
                default:
                    response = "Вы использовали какой-то другой метод";
            }
            sendText(httpExchange, response, code);
        } catch (NotFoundException e) {
            httpExchange.sendResponseHeaders(404, 0);
            response = "Такой подзадачи нет";
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (TimeConflictException t) {
            httpExchange.sendResponseHeaders(406, 0);
            response = "Подзадача пересекается по времени с другой задачей";
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
