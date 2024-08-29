package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedTaskHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedTaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Началась обработка /prioritized запроса от клиента.");
        String response;
        try {
            if (exchange.getRequestMethod().equals("GET")) {
                List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                response = gson.toJson(prioritizedTasks);
                sendText(exchange, response, 200);
                System.out.println("Получен список задач по приоритету");
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendText(exchange, "Ошибка при обработке запроса", 500);
        } finally {
            exchange.close();
        }
    }
}