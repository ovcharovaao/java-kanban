package tasks;

import managers.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SubtaskTest {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    void tasksWithEqualIdShouldBeEqual() {
        Epic epic = new Epic("Name", "Description");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Name1", "Description1", epic.getID());
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Name2", "Description2", epic.getID());
        taskManager.addSubtask(subtask2);

        int idSubtask1 = subtask1.getID();
        int idSubtask2 = subtask2.getID();

        assertNotEquals(idSubtask1, idSubtask2,
                "Экземпляры класса Subtask должны различаться, если у них разные ID");
    }
}