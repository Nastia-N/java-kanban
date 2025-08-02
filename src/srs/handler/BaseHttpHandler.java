package srs.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import srs.manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected TaskManager manager;
    protected Gson gson;

    public BaseHttpHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    protected void sendText(HttpExchange h, String text, int code) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(code, response.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(response);
        }
    }


}