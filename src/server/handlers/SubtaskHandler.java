package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import managers.exception.NotFoundException;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Началась обработка /subtasks запроса от клиента.");
        String path = exchange.getRequestURI().getPath();
        String response;
        try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    if (path.equals("/subtasks")) {
                        List<Subtask> subtasks = taskManager.getSubtasks();
                        response = gson.toJson(subtasks);
                        sendText(exchange, response, 200);
                        System.out.println("Список сабтасков получен");
                    } else {
                        int subtaskID = Integer.parseInt(path.substring("/subtasks/".length()));
                        if (subtaskID == 0) {
                            sendNotFound(exchange);
                            System.out.println("Сабтаск с ID " + subtaskID + " не найден");
                        } else {
                            Subtask subtask = taskManager.getSubtaskByID(subtaskID);
                            response = gson.toJson(subtask);
                            sendText(exchange, response, 200);
                            System.out.println("Сабтаск с ID " + subtaskID + " получен");
                        }
                    }
                    break;
                case "POST":
                    Subtask subtask = gson.fromJson(isr, Subtask.class);
                    if ((subtask.getID() == 0) || (taskManager.getSubtaskByID(subtask.getID()) == null)) {
                        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                        Subtask finalSubtask = subtask;
                        boolean isIntersection = prioritizedTasks.stream()
                                .anyMatch(existingTask ->
                                        finalSubtask.getStartTime().isBefore(existingTask.getEndTime()) &&
                                                finalSubtask.getEndTime().isAfter(existingTask.getStartTime()));

                        if (isIntersection) {
                            sendHasInteractions(exchange);
                        } else {
                            subtask = taskManager.addSubtask(subtask);
                            response = gson.toJson(subtask);
                            sendText(exchange, response, 201);
                            System.out.println("Сабтаск создан");
                        }
                    } else {
                        subtask = taskManager.updateSubtask(subtask);
                        response = gson.toJson(subtask);
                        sendText(exchange, response, 201);
                        System.out.println("Сабтаск обновлен");
                    }
                    break;
                case "DELETE":
                    if (path.equals("/subtasks")) {
                        taskManager.deleteSubtasks();
                        sendText(exchange, "Все сабтаски удалены", 200);
                        System.out.println("Все сабтаски удалены");
                    } else {
                        int subtaskID = Integer.parseInt(path.substring("/subtasks/".length()));
                        taskManager.deleteSubtaskByID(subtaskID);
                        sendText(exchange, "Сабтаск удален", 200);
                        System.out.println("Сабтаск c ID " + subtaskID + " удален");
                    }
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendText(exchange, "Ошибка при обработке запроса", 500);
        } finally {
            exchange.close();
        }
    }
}