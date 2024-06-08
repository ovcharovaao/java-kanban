import tasks.*;
import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {

    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int nextID = 1;

    public Task addTask(Task task) {
        task.setID(nextID);
        nextID++;
        tasks.put(task.getID(), task);
        return task;
    }

    public Epic addEpic(Epic epic) {
        epic.setID(nextID);
        nextID++;
        epics.put(epic.getID(), epic);
        updateEpicStatus(epic);
        return epic;
    }

    public Subtask addSubtask(Subtask subtask) {
        subtask.setID(nextID);
        nextID++;
        subtasks.put(subtask.getID(), subtask);

        Epic epic = epics.get(subtask.getEpicID());
        epic.addSubtaskList(subtask);
        updateEpicStatus(epic);

        return subtask;
    }

    public Task updateTask(Task task) {
        tasks.put(task.getID(), task);
        return task;
    }

    public Epic updateEpic(Epic epic) {
        epics.put(epic.getID(), epic);
        updateEpicStatus(epic);
        return epic;
    }

    public Subtask updateSubtask(Subtask subtask) {
        Subtask oldSubtask = subtasks.get(subtask.getID());
        Epic epic = epics.get(subtask.getEpicID());
        ArrayList<Subtask> subtaskList = epic.getSubtaskList();
        subtaskList.remove(oldSubtask);
        subtaskList.add(subtask);
        epic.setSubtaskList(subtaskList);

        subtasks.put(subtask.getID(), subtask);
        updateEpicStatus(epics.get(subtask.getEpicID()));
        return subtask;
    }

    public Task getTaskByID(int id) {
        return tasks.get(id);
    }

    public Epic getEpicByID(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskByID(int id) {
        return subtasks.get(id);
    }

    public void printTasks() {
        System.out.println(tasks.values());
    }

    public void printEpics() {
        System.out.println(epics.values());
    }

    public void printSubtasks() {
        System.out.println(subtasks.values());
    }

    public ArrayList<Subtask> printEpicSubtasks(Epic epic) {
        return epic.getSubtaskList();
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskList();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    public void deleteTaskByID(int id) {
        tasks.remove(id);
    }

    public void deleteEpicByID(int id) {
        ArrayList<Subtask> subtaskList = epics.get(id).getSubtaskList();
        for (Subtask subtask : subtaskList) {
            subtasks.remove(subtask.getID());
        }
        epics.remove(id);
    }

    public void deleteSubtaskByID(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicID());
        ArrayList<Subtask> subtaskList = epic.getSubtaskList();
        subtaskList.remove(subtask);

        subtasks.remove(id);

        updateEpicStatus(epics.get(subtask.getEpicID()));
    }

    private static void updateEpicStatus(Epic epic) {
        if (epic.getSubtaskList().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        int allSubtasksDone = 0;
        int allSubtasksNew = 0;

        ArrayList<Subtask> allSubtasks = epic.getSubtaskList();

        for (Subtask subtask : allSubtasks) {
            if (subtask.getStatus() == TaskStatus.DONE) {
                allSubtasksDone++;
            }
            if (subtask.getStatus() == TaskStatus.NEW) {
                allSubtasksNew++;
            }
        }

        if (allSubtasksDone == allSubtasks.size()) {
            epic.setStatus(TaskStatus.DONE);
        } else if (allSubtasksNew == allSubtasks.size()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}