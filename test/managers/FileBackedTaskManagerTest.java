package managers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    File tempFile;
    private FileBackedTaskManager fileBackedTaskManager;

    @BeforeEach
    void beforeEach() throws IOException {
        tempFile = File.createTempFile("TestFile", ".csv");
        fileBackedTaskManager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void afterEach() {
        tempFile.deleteOnExit();
    }

    @Test
    void testSaveAndLoadEmptyFile() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getTasks().isEmpty(), "Список тасков должент быть пуст.");
        assertTrue(loadedManager.getEpics().isEmpty(), "Список эпиков должен быть пуст.");
        assertTrue(loadedManager.getSubtasks().isEmpty(), "Список сабтасков должен быть пуст.");
    }

    @Test
    void shouldSaveAndLoadTasks() {
        Task task = new Task("Task", "Description");
        fileBackedTaskManager.addTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Task loadedTask = loadedManager.getTaskByID(task.getID());

        assertNotNull(loadedTask);
        assertEquals(task, loadedTask, "Таски не совпадают.");
    }

    @Test
    void shouldSaveAndLoadEpics() {
        Epic epic = new Epic("Epic", "Description");
        fileBackedTaskManager.addEpic(epic);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Epic loadedEpic = loadedManager.getEpicByID(epic.getID());

        assertNotNull(loadedEpic);
        assertEquals(epic, loadedEpic, "Эпики не совпадают.");
    }

    @Test
    void shouldSaveAndLoadSubtasks() {
        Epic epic = new Epic("Epic","Description");
        fileBackedTaskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask","Description", epic.getID());
        fileBackedTaskManager.addSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Subtask loadedSubtask = loadedManager.getSubtaskByID(subtask.getID());

        assertNotNull(loadedSubtask);
        assertEquals(subtask, loadedSubtask, "Сабтаски не совпадают.");
    }

    @Test
    void shouldSaveAndLoadChangesInFile() throws IOException {
        Epic epic = new Epic("Epic","Description");
        fileBackedTaskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask","Description", epic.getID());
        fileBackedTaskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask","Description", epic.getID());
        fileBackedTaskManager.addSubtask(subtask2);

        fileBackedTaskManager.deleteEpics();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<String> lines = Files.readAllLines(tempFile.toPath());

        assertTrue(loadedManager.getEpics().isEmpty(), "Список эпиков должен быть пуст.");
        assertTrue(loadedManager.getSubtasks().isEmpty(), "Список сабтасков должен быть пуст.");
        assertLinesMatch(lines, Collections.singletonList("id,type,name,status,description,epic"));
    }
}