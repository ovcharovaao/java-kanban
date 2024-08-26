package managers;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CSVFormat {
    static String toString(Task task) {
        return task.toString();
    }

    static Task fromString(String value) {
        String[] parts = value.split(",");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];
        LocalDateTime startTime;
        Duration duration;
        if (parts[5].equals("null") && parts[6].equals("null")) {
            startTime = null;
            duration = null;
        } else {
            startTime = LocalDateTime.parse(parts[5], formatter);
            duration = Duration.parse(parts[6]);
        }

        switch (type) {
            case TASK:
                Task task = new Task(name, description, startTime, duration);
                task.setID(id);
                task.setStatus(status);
                task.setStartTime(startTime);
                task.setDuration(duration);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setID(id);
                epic.setStatus(status);
                epic.setStartTime(startTime);
                epic.setDuration(duration);
                return epic;
            case SUBTASK:
                int epicID = Integer.parseInt(parts[7]);
                Subtask subtask = new Subtask(name, description, epicID, startTime, duration);
                subtask.setID(id);
                subtask.setStatus(status);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }
}
