package managers;

import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    Task task;
    InMemoryTaskManager taskManager;

    @Test
    void add() {
        historyManager.add(task);
        final ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void getHistoryShouldReturnOnly10Tasks() {
        for (int i = 0; i < 12; i++) {
            historyManager.add(task);
        }

        ArrayList<Task> history = historyManager.getHistory();

        assertEquals(10, history.size(), "Неверное количество просмотренных задач в истории");
    }
}