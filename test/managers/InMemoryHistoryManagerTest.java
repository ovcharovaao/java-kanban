package managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    static Task task;
    static Task task1;
    static Epic epic;

    @BeforeAll
    static void createTasks() {
        task = new Task("Name", "Description");
        task1 = new Task("Name1", "Description1");
        epic = new Epic("Name", "Description");

        task.setID(1);
        task1.setID(2);
        epic.setID(3);
    }

    @Test
    void add() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void remove() {
        historyManager.add(task);
        historyManager.add(task1);
        historyManager.add(epic);
        historyManager.remove(epic.getID());
        List<Task> list = historyManager.getHistory();
        Assertions.assertEquals(list.size(), 2);
    }

    @Test
    void getHistory() {
        historyManager.add(task);
        historyManager.add(epic);
        List<Task> list = historyManager.getHistory();
        Assertions.assertEquals(list.size(), 2);
    }

    @Test
    void shouldBeNoDuplicatesInHistory() {
        historyManager.add(task);
        historyManager.add(task1);
        historyManager.add(epic);
        historyManager.add(task1);
        historyManager.add(task);
        assertEquals(historyManager.getHistory(), List.of(task, task1, epic));
    }
}