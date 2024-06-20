package tasks;

import managers.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EpicTest {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    void tasksWithEqualIdShouldBeEqual() {
        Epic epic1 = new Epic("Name1", "Description1");
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic("Name2", "Description2");
        taskManager.addEpic(epic2);

        int idEpic1 = epic1.getID();
        int idEpic2 = epic2.getID();

        assertNotEquals(idEpic1, idEpic2,
                "Экземпляры класса Epic должны различаться, если у них разные ID");
    }
}