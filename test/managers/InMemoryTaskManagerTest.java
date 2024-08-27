package managers;

import org.junit.jupiter.api.Test;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    InMemoryTaskManagerTest() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void getHistoryShouldReturnAllTasks() {
        taskManager.getTaskByID(1);
        taskManager.getEpicByID(2);
        taskManager.getSubtaskByID(3);

        List<Task> historyList = taskManager.getHistory();

        assertEquals(3, taskManager.getHistory().size());
        assertEquals(subtask1, historyList.get(0), "Задачи не совпадают");
        assertEquals(epic, historyList.get(1), "Задачи не совпадают");
        assertEquals(task, historyList.get(2), "Задачи не совпадают");
    }

    @Test
    void tasksTimeIntersectionTest() {
        task.setStartTime(LocalDateTime.of(2024, 8, 25, 10, 0));
        assertThrows(RuntimeException.class, () -> taskManager.updateTask(task),
                "Задачи пересекаются по времени выполнения.");
    }
}