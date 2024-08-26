package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private final int epicID;

    public Subtask(String name, String description, int epicID, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        this.epicID = epicID;
        this.type = TaskType.SUBTASK;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return getID() + "," + TaskType.SUBTASK + "," + getName() + "," + getStatus() + ","
                + getDescription() + "," + getStartTime() + "," + getDuration() + "," + getEpicID();
    }
}