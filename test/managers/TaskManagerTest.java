package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;
    Task task;
    Epic epic;
    Subtask subtask1;
    Subtask subtask2;

    @BeforeEach
    void beforeEach() {
        task = new Task("Test addNewTask", "Test addNewTask description",
                LocalDateTime.of(2024, 8, 25, 9, 30), Duration.ofMinutes(20));
        taskManager.addTask(task);
        epic = new Epic("Epic", "Description");
        taskManager.addEpic(epic);
        subtask1 = new Subtask("Subtask", "Description", epic.getID(),
                LocalDateTime.of(2024, 8, 25, 10, 0), Duration.ofMinutes(30));
        taskManager.addSubtask(subtask1);
        subtask2 = new Subtask("Subtask", "Description", epic.getID(),
                LocalDateTime.of(2024, 8, 25, 10, 30), Duration.ofMinutes(30));
        taskManager.addSubtask(subtask2);
    }

    @Test
    void addTask() {
        Task savedTask = taskManager.getTaskByID(task.getID());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        ArrayList<Task> tasks = (ArrayList<Task>) taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void newTaskAndAddedTaskInManagerShouldBeEquals() {
        assertEquals(task, taskManager.getTaskByID(1), "Задачи не совпадают");
    }

    @Test
    void deleteTasksTest() {
        taskManager.deleteTasks();
        ArrayList<Task> tasks = (ArrayList<Task>) taskManager.getTasks();
        assertTrue(tasks.isEmpty(), "Список тасков должен быть пуст.");
    }

    @Test
    void deleteEpicsTest() {
        taskManager.deleteEpics();
        ArrayList<Epic> epics = (ArrayList<Epic>) taskManager.getEpics();
        ArrayList<Subtask> subtasks = (ArrayList<Subtask>) taskManager.getSubtasks();

        assertTrue(epics.isEmpty(), "Список эпиков должен быть пуст.");
        assertTrue(subtasks.isEmpty(), "После удаления всех эпиков список сабтасков должен быть пуст.");
    }

    @Test
    void deleteSubtasksTest() {
        taskManager.deleteSubtasks();
        ArrayList<Subtask> subtasks = (ArrayList<Subtask>) taskManager.getSubtasks();
        assertTrue(subtasks.isEmpty(), "Список тасков должен быть пуст.");
    }

    @Test
    void deleteTaskByIDTest() {
        taskManager.deleteTaskByID(task.getID());
        ArrayList<Task> tasks = (ArrayList<Task>) taskManager.getTasks();

        assertTrue(tasks.isEmpty(), "Список тасков должен быть пуст.");
    }

    @Test
    void deleteSubtaskByIDTest() {
        taskManager.deleteSubtaskByID(subtask1.getID());

        assertEquals(taskManager.getEpicSubtasks(epic), List.of(subtask2));
    }

    @Test
    void updateTaskTest() {
        task.setDescription("NewDescription");
        taskManager.updateTask(task);

        assertEquals(task.getDescription(), "NewDescription", "Задача не обновилась");
    }

    @Test
    void updateSubtaskTest() {
        subtask1.setName("NewName");
        taskManager.updateSubtask(subtask1);

        assertEquals(subtask1.getName(), "NewName", "Сабтаск не обновился");
    }

    @Test
    void getEpicSubtasksTest() {
        ArrayList<Subtask> subtasks = (ArrayList<Subtask>) taskManager.getEpicSubtasks(epic);

        assertEquals(2, subtasks.size(), "Неверное количество сабтасков эпика");
    }

    @Test
    void AllSubtasksShouldHaveEpic() {
        assertNotNull(taskManager.getEpicByID(subtask1.getEpicID()));
        assertEquals(epic, taskManager.getEpicByID(subtask1.getEpicID()), "Эпики не совпадают");
    }

    @Test
    public void updateEpicStatusToNewTestIfAllSubtaskAreNew() {
        subtask1.setStatus(TaskStatus.NEW);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(TaskStatus.NEW);
        taskManager.updateSubtask(subtask2);
        assertEquals(TaskStatus.NEW, taskManager.getEpicByID(epic.getID()).getStatus());
    }

    @Test
    public void updateEpicStatusToDoneTestIfAllSubtaskAreDone() {
        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask2);
        assertEquals(TaskStatus.DONE, taskManager.getEpicByID(epic.getID()).getStatus());
    }

    @Test
    public void updateEpicStatusToInProgressTestIfOneSubtaskIsNew() {
        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(TaskStatus.NEW);
        taskManager.updateSubtask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicByID(epic.getID()).getStatus());
    }

    @Test
    public void shouldThrowExceptionWhenSubtasksIntersection() {
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        assertThrows(RuntimeException.class, () -> taskManager.updateSubtask(subtask2),
                "Задачи пересекаются по времени выполнения.");
    }
}