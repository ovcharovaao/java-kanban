package managers;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    void addTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final Task savedTask = taskManager.addTask(task);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = taskManager.printTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void getHistoryShouldReturnAllTasks() {
        Task task = new Task("Name", "Description");
        taskManager.addTask(task);
        Epic epic = new Epic("Name", "Description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Name", "Description", epic.getID());
        taskManager.addSubtask(subtask);

        taskManager.getTaskByID(1);
        taskManager.getEpicByID(2);
        taskManager.getSubtaskByID(3);

        List<Task> historyList = taskManager.getHistory();

        assertEquals(3, taskManager.getHistory().size());
        assertEquals(task, historyList.get(0), "Задачи не совпадают");
        assertEquals(epic, historyList.get(1), "Задачи не совпадают");
        assertEquals(subtask, historyList.get(2), "Задачи не совпадают");
    }

    @Test
    void newTaskAndAddedTaskInManagerShouldBeEquals() {
        Task task = new Task("Name", "Description");
        taskManager.addTask(task);

        assertEquals(task, taskManager.getTaskByID(1), "Задачи не совпадают");
    }

    @Test
    void deleteEpics() {
        Epic epic = new Epic("Name", "Description");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Name1", "Description1", epic.getID());
        Subtask subtask2 = new Subtask("Name2", "Description2", epic.getID());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        taskManager.deleteEpics();
        ArrayList<Epic> epics = taskManager.printEpics();
        ArrayList<Subtask> subtasks = taskManager.printSubtasks();

        assertTrue(epics.isEmpty(), "Список эпиков должен быть пуст.");
        assertTrue(subtasks.isEmpty(), "После удаления всех эпиков список сабтасков должен быть пуст.");
    }

    @Test
    void deleteTaskByID() {
        Task task = new Task("Name", "Description");
        taskManager.addTask(task);
        taskManager.deleteTaskByID(task.getID());

        ArrayList<Task> tasks = taskManager.printTasks();
        assertTrue(tasks.isEmpty(), "Список тасков должен быть пуст.");
    }

    @Test
    void deleteSubtaskByID() {
        Epic epic = new Epic("Name", "Description");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Name1", "Description1", epic.getID());
        Subtask subtask2 = new Subtask("Name2", "Description2", epic.getID());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        taskManager.deleteSubtaskByID(subtask1.getID());

        assertEquals(taskManager.printEpicSubtasks(epic), List.of(subtask2));
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Name", "Description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Name", "Description", epic.getID());
        taskManager.addSubtask(subtask);

        Subtask updatedSubtask = new Subtask("NewName", "NewDescription", epic.getID());
        taskManager.updateSubtask(updatedSubtask);

        assertEquals(updatedSubtask, taskManager.getSubtaskByID(0), "Сабтаск не обновился");
    }

    @Test
    void printEpicSubtasks() {
        Epic epic = new Epic("Name", "Description");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Name1", "Description1", epic.getID());
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Name2", "Description2", epic.getID());
        taskManager.addSubtask(subtask2);

        ArrayList<Subtask> subtasks = taskManager.printEpicSubtasks(epic);

        assertEquals(2, subtasks.size(), "Неверное количество сабтасков эпика");
    }
}