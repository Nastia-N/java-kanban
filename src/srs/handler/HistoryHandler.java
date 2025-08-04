package srs.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import srs.manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "";

        String method = httpExchange.getRequestMethod();
        int code = 0;

        if (method.equals("GET")) {
            response = gson.toJson(manager.getHistory());
            code = 200;
        } else {
            response = "Вы использовали неизвестный метод";
        }
        sendText(httpExchange, response, code);
    }
}
