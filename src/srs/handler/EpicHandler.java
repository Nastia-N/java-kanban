package srs.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import srs.manager.TaskManager;
import srs.manager.TimeConflictException;
import srs.model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    public EpicHandler(TaskManager manager, Gson gson) {
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

            switch (method) {
                case "POST":
                    response = gson.toJson(manager.createEpic(newBody.getName(), newBody.getDescription()));
                    httpExchange.sendResponseHeaders(201, 0);
                    break;
                case "GET":
                    if (splitStrings.length == 4) {
                        String str = splitStrings[2];
                        int id = Integer.parseInt(str);
                        response = gson.toJson(manager.getEpic(id));
                    } else if (splitStrings.length == 3) {
                        String str = splitStrings[2];
                        int id = Integer.parseInt(str);
                        response = gson.toJson(manager.getSubtasksByEpic(id));
                    } else {
                        response = gson.toJson(manager.getAllEpics());
                    }
                    httpExchange.sendResponseHeaders(200, 0);
                    break;
                case "DELETE":
                    String str = splitStrings[2];
                    int id = Integer.parseInt(str);
                    manager.deleteEpic(id);
                    httpExchange.sendResponseHeaders(200, 0);
                    break;
                default:
                    response = "Вы использовали какой-то другой метод";
            }
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (NotFoundException e) {
            httpExchange.sendResponseHeaders(404, 0);
            response = "Такого эпика нет";
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (TimeConflictException t) {
            httpExchange.sendResponseHeaders(406, 0);
            response = "Эпик пересекается по времени с другой задачей";
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}

