package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private int nextID = 1;

    @Override
    public Task addTask(Task task) {
        task.setID(nextID);
        nextID++;
        tasks.put(task.getID(), task);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setID(nextID);
        nextID++;
        epics.put(epic.getID(), epic);
        updateEpicStatus(epic);
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        subtask.setID(nextID);
        nextID++;
        subtasks.put(subtask.getID(), subtask);

        Epic epic = epics.get(subtask.getEpicID());
        epic.addSubtaskList(subtask);
        updateEpicStatus(epic);

        return subtask;
    }

    @Override
    public Task updateTask(Task task) {
        tasks.put(task.getID(), task);
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        epics.put(epic.getID(), epic);
        updateEpicStatus(epic);
        return epic;
    }

    @Override
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

    @Override
    public Task getTaskByID(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        return epic.getSubtaskList();
    }

    @Override
    public void deleteTasks() {
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }
        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskList();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public void deleteTaskByID(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void deleteEpicByID(int id) {
        historyManager.remove(id);
        for (int keySet : subtasks.keySet()) {
            historyManager.remove(keySet);
        }
        ArrayList<Subtask> subtaskList = epics.get(id).getSubtaskList();
        for (Subtask subtask : subtaskList) {
            subtasks.remove(subtask.getID());
        }
        epics.remove(id);
    }

    @Override
    public void deleteSubtaskByID(int id) {
        historyManager.remove(id);
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicID());
        ArrayList<Subtask> subtaskList = epic.getSubtaskList();
        subtaskList.remove(subtask);

        subtasks.remove(id);

        updateEpicStatus(epics.get(subtask.getEpicID()));
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
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