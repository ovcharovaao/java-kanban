package managers;

import managers.exception.NotFoundException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {
    Task addTask(Task task);

    Epic addEpic(Epic epic);

    Subtask addSubtask(Subtask subtask);

    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask);

    Task getTaskByID(int id) throws NotFoundException;

    Epic getEpicByID(int id) throws NotFoundException;

    Subtask getSubtaskByID(int id) throws NotFoundException;

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    List<Subtask> getEpicSubtasks(Epic epic);

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    void deleteTaskByID(int id) throws NotFoundException;

    void deleteEpicByID(int id) ;

    void deleteSubtaskByID(int id) throws NotFoundException;

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}