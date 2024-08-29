package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import server.HttpTaskServer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    TaskManager taskManager;
    Gson gson;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = HttpTaskServer.getGson();
    }

    protected void sendText(HttpExchange h, String text, int code) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        h.sendResponseHeaders(code, response.length);
        h.getResponseBody().write(response);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        String response = "Не найдено";
        sendText(h, response, 404);
        h.sendResponseHeaders(404, response.length());
    }

    protected void sendHasInteractions(HttpExchange h) throws IOException {
        String response = "Задача пересекается с существующей задачей";
        sendText(h, response, 406);
        h.sendResponseHeaders(406, response.length());
    }
}