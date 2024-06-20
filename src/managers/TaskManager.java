package managers;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import java.util.ArrayList;

public interface TaskManager {
    Task addTask(Task task);

    Epic addEpic(Epic epic);

    Subtask addSubtask(Subtask subtask);

    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask);

    Task getTaskByID(int id);

    Epic getEpicByID(int id);

    Subtask getSubtaskByID(int id);

    ArrayList<Task> printTasks();

    ArrayList<Epic> printEpics();

    ArrayList<Subtask> printSubtasks();

    ArrayList<Subtask> printEpicSubtasks(Epic epic);

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    void deleteTaskByID(int id);

    void deleteEpicByID(int id);

    void deleteSubtaskByID(int id);

    ArrayList<Task> getHistory();
}