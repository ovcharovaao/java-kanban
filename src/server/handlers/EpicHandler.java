package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import managers.exception.NotFoundException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Началась обработка /epics запроса от клиента.");
        String path = exchange.getRequestURI().getPath();
        String response;
        try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    if (path.equals("/epics")) {
                        List<Epic> epics = taskManager.getEpics();
                        response = gson.toJson(epics);
                        sendText(exchange, response, 200);
                        System.out.println("Список эпиков получен");
                    } else if (path.startsWith("/epics/") && !(path.endsWith("/subtasks"))) {
                            int epicID = Integer.parseInt(path.substring("/epics/".length()));
                            Epic epic = taskManager.getEpicByID(epicID);
                            response = gson.toJson(epic);
                            sendText(exchange, response, 200);
                            System.out.println("Эпик получен");
                    } else if (path.startsWith("/epics/") && path.endsWith("/subtasks")) {
                        int epicID = Integer.parseInt(path.substring("/epics/".length(),
                                path.lastIndexOf("/subtasks")));
                        Epic epic = taskManager.getEpicByID(epicID);
                        List<Subtask> subtasks = taskManager.getEpicSubtasks(epic);
                        response = gson.toJson(subtasks);
                        sendText(exchange, response, 200);
                        System.out.println("Список сабтасков эпика получен");
                    }
                    break;
                case "POST":
                    Epic epic = gson.fromJson(isr, Epic.class);
                    List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                    Epic finalEpic = epic;
                    boolean isIntersection = prioritizedTasks.stream()
                            .anyMatch(existingTask ->
                                    finalEpic.getStartTime().isBefore(existingTask.getEndTime()) &&
                                            finalEpic.getEndTime().isAfter(existingTask.getStartTime()));

                    if (isIntersection) {
                        sendHasInteractions(exchange);
                    } else {
                        if ((epic.getID() == 0) || (taskManager.getEpicByID(epic.getID()) == null)) {
                            epic = taskManager.addEpic(epic);
                            response = gson.toJson(epic);
                            sendText(exchange, response, 201);
                            System.out.println("Эпик создан");
                        } else {
                            epic = taskManager.updateEpic(epic);
                            response = gson.toJson(epic);
                            sendText(exchange, response, 201);
                            System.out.println("Эпик обновлен");
                        }
                    }
                    break;
                case "DELETE":
                    if (path.equals("/epics")) {
                        taskManager.deleteEpics();
                        sendText(exchange, "Все эпики удалены", 200);
                        System.out.println("Все эпики удалены");
                    } else {
                        int epicID = Integer.parseInt(path.substring("/epics/".length()));
                        taskManager.deleteEpicByID(epicID);
                        sendText(exchange, "Эпик удален", 200);
                        System.out.println("Эпик c ID " + epicID + " удален");
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