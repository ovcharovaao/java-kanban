package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import managers.exception.NotFoundException;
import tasks.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Началась обработка /tasks запроса от клиента.");
        String path = exchange.getRequestURI().getPath();
        String response;

        try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    if (path.equals("/tasks")) {
                        List<Task> tasks = taskManager.getTasks();
                        response = gson.toJson(tasks);
                        sendText(exchange, response, 200);
                        System.out.println("Список задач получен");
                    } else {
                        int taskID = Integer.parseInt(path.substring("/tasks/".length()));
                        if (taskID == 0) {
                            sendNotFound(exchange);
                            System.out.println("Задача с ID " + taskID + " не найдена");
                        } else {
                            Task task = taskManager.getTaskByID(taskID);
                            response = gson.toJson(task);
                            sendText(exchange, response, 200);
                            System.out.println("Задача с ID " + taskID + " получена");
                        }
                    }
                    break;
                case "POST":
                    Task task = gson.fromJson(isr, Task.class);
                    if ((task.getID() == 0) || (taskManager.getTaskByID(task.getID()) == null)) {
                        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                        Task finalTask = task;
                        boolean isIntersection = prioritizedTasks.stream()
                                .anyMatch(existingTask ->
                                        finalTask.getStartTime().isBefore(existingTask.getEndTime()) &&
                                                finalTask.getEndTime().isAfter(existingTask.getStartTime()));
                        if (isIntersection) {
                            sendHasInteractions(exchange);
                        } else {
                            task = taskManager.addTask(task);
                            response = gson.toJson(task);
                            sendText(exchange, response, 201);
                            System.out.println("Задача создана");
                        }
                    } else {
                        task = taskManager.updateTask(task);
                        response = gson.toJson(task);
                        sendText(exchange, response, 201);
                        System.out.println("Задача обновлена");
                    }
                    break;
                case "DELETE":
                    if (path.equals("/tasks")) {
                        taskManager.deleteTasks();
                        sendText(exchange, "Все задачи удалены", 200);
                        System.out.println("Все задачи удалены");
                    } else {
                        int taskID = Integer.parseInt(path.substring("/tasks/".length()));
                        taskManager.deleteTaskByID(taskID);
                        sendText(exchange, "Задача удалена", 200);
                        System.out.println("Задача c ID " + taskID + " удалена");
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