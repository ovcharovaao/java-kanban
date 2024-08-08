package managers;

import tasks.*;

public class CSVFormat {
    static String toString(Task task) {
        return task.toString();
    }

    static Task fromString(String value) {
        String[] parts = value.split(",");

        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];

        switch (type) {
            case TASK:
                Task task = new Task(name, description);
                task.setID(id);
                task.setStatus(status);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setID(id);
                epic.setStatus(status);
                return epic;
            case SUBTASK:
                int epicID = Integer.parseInt(parts[5]);
                Subtask subtask = new Subtask(name, description, epicID);
                subtask.setID(id);
                subtask.setStatus(status);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }
}
