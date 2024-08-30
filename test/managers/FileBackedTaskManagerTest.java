package managers;

import managers.exception.ManagerSaveException;
import managers.exception.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    File tempFile;

    FileBackedTaskManagerTest() throws Exception {
        tempFile = File.createTempFile("TestFile", ".csv");
        taskManager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void afterEach() {
        tempFile.deleteOnExit();
    }

    @Test
    void testSaveAndLoadEmptyFile() throws IOException {
        File emptyTempFile = File.createTempFile("TestFile", ".csv");
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(emptyTempFile);

        assertTrue(loadedManager.getTasks().isEmpty(), "Список тасков должент быть пуст.");
        assertTrue(loadedManager.getEpics().isEmpty(), "Список эпиков должен быть пуст.");
        assertTrue(loadedManager.getSubtasks().isEmpty(), "Список сабтасков должен быть пуст.");
    }

    @Test
    void shouldSaveAndLoadTasks() throws NotFoundException {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Task loadedTask = loadedManager.getTaskByID(task.getID());

        assertNotNull(loadedTask);
        assertEquals(task, loadedTask, "Таски не совпадают.");
    }

    @Test
    void shouldSaveAndLoadEpics() throws NotFoundException {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Epic loadedEpic = loadedManager.getEpicByID(epic.getID());

        assertNotNull(loadedEpic);
        assertEquals(epic, loadedEpic, "Эпики не совпадают.");
    }

    @Test
    void shouldSaveAndLoadSubtasks() throws NotFoundException {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Subtask loadedSubtask = loadedManager.getSubtaskByID(subtask1.getID());

        assertNotNull(loadedSubtask);
        assertEquals(subtask1, loadedSubtask, "Сабтаски не совпадают.");
    }

    @Test
    void shouldSaveAndLoadChangesInFile() {
        taskManager.deleteEpics();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getEpics().isEmpty(), "Список эпиков должен быть пуст.");
        assertTrue(loadedManager.getSubtasks().isEmpty(), "Список сабтасков должен быть пуст.");
    }

    @Test
    public void shouldThrowExceptionOnInvalidFile() {
        File invalidFile = new File(tempFile.getParent(), "invalidFile.csv");

        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(invalidFile);
        });

        invalidFile.deleteOnExit();
    }
}