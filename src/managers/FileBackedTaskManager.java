package managers;

import tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("id,type,name,status,description,epic\n");

            for (Task task : getTasks()) {
                bw.write(CSVFormat.toString(task) + "\n");
            }

            for (Epic epic : getEpics()) {
                bw.write(CSVFormat.toString(epic) + "\n");
            }

            for (Subtask subtask : getSubtasks()) {
                bw.write(CSVFormat.toString(subtask) + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            List<String> tasksInStrings = Files.readAllLines(file.toPath());

            for (int i = 1; i < tasksInStrings.size(); i++) {
                Task task = CSVFormat.fromString(tasksInStrings.get(i));
                switch (task.getTaskType()) {
                    case TASK:
                        manager.addTask(task);
                        break;
                    case EPIC:
                        manager.addEpic((Epic) task);
                        break;
                    case SUBTASK:
                        manager.addSubtask((Subtask) task);
                        break;
                    default:
                        throw new IllegalArgumentException("Неизвестный тип задачи: " + task.getTaskType());
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения данных из файла", e);
        }

        return manager;
    }

    @Override
    public Task addTask(Task task) {
        super.addTask(task);
        save();
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public Task updateTask(Task task) {
        super.updateTask(task);
        save();
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteTaskByID(int id) {
        super.deleteTaskByID(id);
        save();
    }

    @Override
    public void deleteEpicByID(int id) {
        super.deleteEpicByID(id);
        save();
    }

    @Override
    public void deleteSubtaskByID(int id) {
        super.deleteSubtaskByID(id);
        save();
    }

    public static void main(String[] args) throws IOException {
        File tempFile = File.createTempFile("TestFile", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Task1", "Description1");
        manager.addTask(task1);

        Epic epic1 = new Epic("Epic1", "Description1");
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask1", "Description", epic1.getID());
        manager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask2", "Description", epic1.getID());
        manager.addSubtask(subtask2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> taskList = loadedManager.getTasks();

        for (Task task : taskList) {
            System.out.println(task);
        }

        List<Epic> epicList = loadedManager.getEpics();

        for (Epic epic : epicList) {
            System.out.println(epic);
        }

        List<Subtask> subtaskList = loadedManager.getSubtasks();

        for (Subtask subtask : subtaskList) {
            System.out.println(subtask);
        }

        tempFile.deleteOnExit();
    }
}