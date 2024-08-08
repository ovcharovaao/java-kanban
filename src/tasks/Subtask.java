package tasks;

public class Subtask extends Task {

    private final int epicID;

    public Subtask(String name, String description, int epicID) {
        super(name, description);
        this.epicID = epicID;
        this.type = TaskType.SUBTASK;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return getID() + "," + TaskType.SUBTASK + "," + getName() + "," + getStatus() + ","
                + getDescription() + "," + getEpicID();
    }
}