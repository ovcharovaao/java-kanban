package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import managers.*;
import managers.exception.NotFoundException;
import tasks.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
        manager.getPrioritizedTasks().clear();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("NewTask", "NewTask description",
                LocalDateTime.of(2024, 8, 25, 9, 30), Duration.ofMinutes(20));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("NewTask", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException, NotFoundException {
        Task task = new Task("Task", "Description",
                LocalDateTime.of(2024, 8, 25, 9, 30), Duration.ofMinutes(20));
        manager.addTask(task);

        task.setName("UpdatedTask");
        task.setDescription("Updated description");
        String updatedTaskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers
                .ofString(updatedTaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Task updatedTask = manager.getTaskByID(task.getID());
        assertNotNull(updatedTask, "Задача не найдена после обновления");
        assertEquals("UpdatedTask", updatedTask.getName(), "Имя задачи не обновлено");
        assertEquals("Updated description", updatedTask.getDescription(), "Описание задачи не обновлено");
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        Task task = new Task("Task", "Description", LocalDateTime.now(), Duration.ofMinutes(20));
        task = manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        var taskListType = new TypeToken<List<Task>>(){}.getType();
        List<Task> tasks = gson.fromJson(response.body(), taskListType);
        assertTrue(tasks.contains(task), "Задача не найдена в списке возвращенных задач");
    }

    @Test
    public void testGetTaskByID() throws IOException, InterruptedException {
        Task task = new Task("Task", "Description", LocalDateTime.now(), Duration.ofMinutes(20));
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getID());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task receivedTask = gson.fromJson(response.body(), Task.class);
        assertEquals(task, receivedTask, "Задача не найдена или не соответствует ожидаемой");
    }

    @Test
    public void testDeleteAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Description1", LocalDateTime.now(), Duration.ofHours(1));
        Task task2 = new Task("Task2", "Description2", LocalDateTime.now().plusHours(1),
                Duration.ofHours(1));
        manager.addTask(task1);
        manager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Все задачи удалены", response.body());

        assertTrue(manager.getTasks().isEmpty(), "Все задачи должны быть удалены");
    }

    @Test
    public void testDeleteTaskByID() throws IOException, InterruptedException, NotFoundException {
        Task task = new Task("Test Task", "Test Description", LocalDateTime.now(), Duration.ofHours(1));
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getID());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача удалена", response.body());

        assertThrows(NotFoundException.class, () -> {
            manager.getTaskByID(task.getID());
        }, "Задача не найдена");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("NewEpic", "NewEpic description");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest
                .BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = manager.getEpics();

        assertNotNull(tasksFromManager, "Эпики не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество эпиков");
        assertEquals("NewEpic", tasksFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException, NotFoundException {
        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);

        epic.setName("UpdatedEpic");
        epic.setDescription("Updated description");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers
                        .ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Epic updatedEpic = manager.getEpicByID(epic.getID());
        assertNotNull(updatedEpic, "Эпик не найден после обновления");
        assertEquals("UpdatedEpic", updatedEpic.getName(), "Имя эпика не обновилось");
        assertEquals("Updated description", updatedEpic.getDescription(), "Описание эпика не обновилось");
    }

    @Test
    public void testDeleteAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "Description1");
        Epic epic2 = new Epic("Epic2", "Description2");
        manager.addTask(epic1);
        manager.addTask(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Все эпики удалены", response.body());

        assertTrue(manager.getEpics().isEmpty(), "Все эпики должны быть удалены");
    }

    @Test
    public void testDeleteEpicByID() throws IOException, InterruptedException, NotFoundException {
        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getID());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertThrows(NotFoundException.class, () -> {

              manager.getTaskByID(epic.getID());
        }, "Задача не найдена");
    }

    @Test
    void testGetAllEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        var epicListType = new TypeToken<List<Epic>>(){}.getType();
        List<Epic> epics = gson.fromJson(response.body(), epicListType);

        assertTrue(epics.contains(epic), "Эпик не найден в списке возвращенных задач");
        assertNotNull(epics, "Список эпиков не должен быть null");
        assertEquals(1, epics.size(), "Должен быть один эпик");
    }

    @Test
    public void testGetEpicByID() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getID());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Epic receivedEpic = gson.fromJson(response.body(), Epic.class);
        assertEquals(epic, receivedEpic, "Эпик не соответствует ожидаемому");
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1",
                epic.getID(), LocalDateTime.of(2024, 8, 25, 9, 30),
                Duration.ofMinutes(20));
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2",
                epic.getID(), LocalDateTime.of(2024, 8, 25, 10, 0),
                Duration.ofMinutes(3));
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getID() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус ответа");

        var subtaskListType = new TypeToken<List<Subtask>>(){}.getType();
        List<Subtask> subtasks = gson.fromJson(response.body(), subtaskListType);
        assertTrue(subtasks.containsAll(Arrays.asList(subtask1, subtask2)),
                "Не все подзадачи найдены в списке возвращенных подзадач");
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException, NotFoundException {
        Task task1 = new Task("Task1", "Description1", LocalDateTime.now(), Duration.ofMinutes(20));
        Task task2 = new Task("Task2", "Description2", LocalDateTime.now().plusHours(1),
                Duration.ofMinutes(30));
        manager.addTask(task1);
        manager.addTask(task2);

        manager.getTaskByID(task1.getID());
        manager.getTaskByID(task2.getID());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус код");

        var historyListType = new TypeToken<List<Task>>(){}.getType();
        List<Task> history = gson.fromJson(response.body(), historyListType);

        assertTrue(history.contains(task1), "Задача1 не сохранилась в истории");
        assertTrue(history.contains(task2), "Задача2 не сохранилась в истории");
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Test addNewTask", "Test addNewTask description",
                LocalDateTime.of(2024, 8, 25, 9, 30), Duration.ofMinutes(20));
        Task task2 = new Task("Test addNewTask", "Test addNewTask description",
                LocalDateTime.of(2024, 8, 25, 10, 00), Duration.ofMinutes(30));
        manager.addTask(task1);
        manager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        var taskListType = new TypeToken<List<Task>>(){}.getType();
        List<Task> tasks = gson.fromJson(response.body(), taskListType);

        assertTrue(tasks.contains(task1), "Приоритетная задача 1 не найдена в списке возвращенных задач");
        assertTrue(tasks.contains(task2), "Приоритетная задача 2 не найдена в списке возвращенных задач");

        assertEquals(task1, tasks.get(0), "Задача1 должна быть первой в списке");
        assertEquals(task2, tasks.get(1), "Задача2 должна быть второй в списке");
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("NewSubask", "NewTask description", epic.getID(),
                LocalDateTime.of(2024, 8, 25, 9, 30), Duration.ofMinutes(20));
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.
                BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> subtasks = manager.getSubtasks();

        assertNotNull(subtasks, "Сабтаски не возвращаются");
        assertEquals(1, subtasks.size(), "Некорректное количество сабтасков");
        assertEquals("NewSubask", subtasks.get(0).getName(), "Некорректное имя сабтаска");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException, NotFoundException {
        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("NewSubask", "NewTask description", epic.getID(),
                LocalDateTime.of(2024, 8, 25, 9, 30), Duration.ofMinutes(20));
        manager.addSubtask(subtask);

        subtask.setName("UpdatedSubtask");
        subtask.setDescription("Updated description");
        String updatedSubtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers
                .ofString(updatedSubtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Subtask updatedSubtask = manager.getSubtaskByID(subtask.getID());
        assertNotNull(updatedSubtask, "Сабтаск не найден после обновления");
        assertEquals("UpdatedSubtask", updatedSubtask.getName(), "Имя сабтаска не обновлено");
        assertEquals("Updated description", updatedSubtask.getDescription(),
                "Описание сабтаска не обновлено");
    }

    @Test
    public void testGetAllSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Description", epic.getID(),
                LocalDateTime.of(2024, 8, 25, 9, 30), Duration.ofMinutes(20));
        manager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        var taskListType = new TypeToken<List<Subtask>>(){}.getType();
        List<Task> subtasks = gson.fromJson(response.body(), taskListType);
        assertTrue(subtasks.contains(subtask), "Сабтаск не найден в списке возвращенных задач");
    }

    @Test
    public void testGetSubtaskByID() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Description", epic.getID(),
                LocalDateTime.of(2024, 8, 25, 9, 30), Duration.ofMinutes(20));
        manager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getID());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Subtask receivedSubtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals(subtask, receivedSubtask, "Сабтаск не найден или не соответствует ожидаемому");
    }

    @Test
    public void testDeleteAllSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask", "Description", epic.getID(),
                LocalDateTime.of(2024, 8, 25, 9, 30), Duration.ofMinutes(20));
        Subtask subtask2 = new Subtask("Subtask", "Description", epic.getID(),
                LocalDateTime.of(2024, 8, 25, 10, 0), Duration.ofMinutes(30));
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Все сабтаски удалены", response.body());

        assertTrue(manager.getSubtasks().isEmpty(), "Все сабтаски должны быть удалены");
    }

    @Test
    public void testDeleteSubtaskByID() throws IOException, InterruptedException, NotFoundException {
        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Description", epic.getID(),
                LocalDateTime.of(2024, 8, 25, 9, 30), Duration.ofMinutes(20));
        manager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getID());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Сабтаск удален", response.body());

        assertThrows(NotFoundException.class, () -> manager.getSubtaskByID(subtask.getID()),
                "Сабтаск не найден");
    }
}