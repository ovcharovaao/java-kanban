package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Началась обработка /history запроса от клиента.");
        String response;
        try {
            if (exchange.getRequestMethod().equals("GET")) {
                List<Task> history = taskManager.getHistory();
                response = gson.toJson(history);
                sendText(exchange, response, 200);
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
