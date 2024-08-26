package tasks;

import managers.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TaskTest {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    void tasksWithEqualIdShouldBeEqual() {
        Task task1 = new Task("Name1", "Description1",
                LocalDateTime.of(2024, 8, 25, 10, 0), Duration.ofMinutes(30));
        taskManager.addTask(task1);
        Task task2 = new Task("Name2", "Description2",
                LocalDateTime.of(2024, 8, 25, 10, 30), Duration.ofMinutes(30));
        taskManager.addTask(task2);

        int idTask1 = task1.getID();
        int idTask2 = task2.getID();

        assertNotEquals(idTask1, idTask2,
                "Экземпляры класса Task должны различаться, если у них разные ID");
    }
}