package managers;

import managers.exception.NotFoundException;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    private int nextID = 1;

    @Override
    public Task addTask(Task task) {
        task.setID(nextID);
        nextID++;
        addPrioritized(task);
        tasks.put(task.getID(), task);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setID(nextID);
        nextID++;
        epics.put(epic.getID(), epic);
        updateEpicStatus(epic);
        updateEpicTime(epic);
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        subtask.setID(nextID);
        nextID++;
        subtasks.put(subtask.getID(), subtask);
        addPrioritized(subtask);

        Epic epic = epics.get(subtask.getEpicID());
        epic.addSubtaskList(subtask);
        updateEpicStatus(epic);
        updateEpicTime(epic);

        return subtask;
    }

    @Override
    public Task updateTask(Task task) {
        Task oldTask = tasks.get(task.getID());

        if (oldTask != null) {
            prioritizedTasks.remove(oldTask);
        }

        tasksTimeIntersection(task);
        addPrioritized(task);
        tasks.put(task.getID(), task);
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        epics.put(epic.getID(), epic);
        updateEpicStatus(epic);
        updateEpicTime(epic);
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask oldSubtask = subtasks.get(subtask.getID());

        if (oldSubtask != null) {
            prioritizedTasks.remove(oldSubtask);
        }

        tasksTimeIntersection(subtask);
        addPrioritized(subtask);

        Epic epic = epics.get(subtask.getEpicID());

        if (epic != null) {
            ArrayList<Subtask> subtaskList = epic.getSubtaskList();
            subtaskList.remove(oldSubtask);
            subtaskList.add(subtask);
            epic.setSubtaskList(subtaskList);
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }

        subtasks.put(subtask.getID(), subtask);
        return subtask;
    }

    @Override
    public Task getTaskByID(int id) throws NotFoundException {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Задача не найдена");
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicByID(int id) throws NotFoundException {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Задача не найдена");
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskByID(int id) throws NotFoundException {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException("Задача не найдена");
        }
        historyManager.add(subtask);
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
        tasks.keySet().forEach(historyManager::remove);
        prioritizedTasks.removeAll(new HashSet<>(tasks.values()));
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        Set<Integer> allEpicRelatedIds = new HashSet<>(epics.keySet());
        allEpicRelatedIds.addAll(subtasks.keySet());
        allEpicRelatedIds.forEach(historyManager::remove);
        prioritizedTasks.removeAll(new HashSet<>(subtasks.values()));
        epics.clear();
        subtasks.clear();
        tasks.entrySet().removeIf(entry -> allEpicRelatedIds.contains(entry.getKey()));
    }

    @Override
    public void deleteSubtasks() {
        subtasks.keySet().forEach(historyManager::remove);
        prioritizedTasks.removeAll(new HashSet<>(subtasks.values()));
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.clearSubtaskList();
            epic.setStatus(TaskStatus.NEW);
            updateEpicTime(epic);
        });
    }

    @Override
    public void deleteTaskByID(int id) throws NotFoundException {
        historyManager.remove(id);
        prioritizedTasks.remove(getTaskByID(id));
        tasks.remove(id);
    }

    @Override
    public void deleteEpicByID(int id) {
        historyManager.remove(id);
        subtasks.keySet().forEach(historyManager::remove);

        epics.get(id).getSubtaskList().stream()
                .map(Subtask::getID)
                .forEach(subtaskId -> {
                    subtasks.remove(subtaskId);
                    historyManager.remove(subtaskId);
                });

        epics.remove(id);
    }

    @Override
    public void deleteSubtaskByID(int id) throws NotFoundException {
        historyManager.remove(id);
        prioritizedTasks.remove(getSubtaskByID(id));
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicID());
        ArrayList<Subtask> subtaskList = epic.getSubtaskList();
        subtaskList.remove(subtask);

        subtasks.remove(id);

        updateEpicStatus(epics.get(subtask.getEpicID()));
        updateEpicTime(epics.get(subtask.getEpicID()));
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void addPrioritized(Task task) {
        if ((task.getStartTime() == null) || (task.getTaskType().equals(TaskType.EPIC))) return;
        tasksTimeIntersection(task);
        prioritizedTasks.add(task);
    }

    private void tasksTimeIntersection(Task task) {
        boolean isIntersection = prioritizedTasks.stream()
                .anyMatch(existingTask ->
                        task.getStartTime().isBefore(existingTask.getEndTime()) &&
                                task.getEndTime().isAfter(existingTask.getStartTime()));
        if (isIntersection) {
            throw new RuntimeException("Задачи пересекаются по времени выполнения.");
        }
    }

    private static void updateEpicStatus(Epic epic) {
        List<Subtask> subtasks = epic.getSubtaskList();
        if (subtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        long countDone = subtasks.stream().filter(subtask -> subtask.getStatus() == TaskStatus.DONE).count();
        long countNew = subtasks.stream().filter(subtask -> subtask.getStatus() == TaskStatus.NEW).count();
        long countInProgress = subtasks.stream().filter(subtask -> subtask.getStatus()
                == TaskStatus.IN_PROGRESS).count();

        if (countInProgress > 1) {
            throw new IllegalStateException("Только одна подзадача может быть в статусе IN_PROGRESS у одного эпика.");
        }

        if (countDone == subtasks.size()) {
            epic.setStatus(TaskStatus.DONE);
        } else if (countNew == subtasks.size()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private static void updateEpicTime(Epic epic) {
        List<Subtask> subtasks = epic.getSubtaskList();
        if (subtasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(null);
        } else {
            epic.setStartTime(subtasks.stream()
                    .map(Subtask::getStartTime)
                    .min(LocalDateTime::compareTo)
                    .orElse(null));

            epic.setEndTime(subtasks.stream()
                    .map(Subtask::getEndTime)
                    .max(LocalDateTime::compareTo)
                    .orElse(null));

            Duration duration = subtasks.stream()
                    .map(Subtask::getDuration)
                    .reduce(Duration.ofMinutes(0), Duration::plus);
            epic.setDuration(duration);
        }
    }
}