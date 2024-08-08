package tasks;

import java.util.ArrayList;

public class Epic extends Task {

    public Epic(String name, String description) {
        super(name, description);
        this.type = TaskType.EPIC;
    }

    private ArrayList<Subtask> subtaskList = new ArrayList<>();

    public ArrayList<Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void setSubtaskList(ArrayList<Subtask> subtaskList) {
        this.subtaskList = subtaskList;
    }

    public void addSubtaskList(Subtask subtask) {
        subtaskList.add(subtask);
    }

    public void clearSubtaskList() {
        subtaskList.clear();
    }

    @Override
    public String toString() {
        return getID() + "," + TaskType.EPIC + "," + getName() + "," + getStatus() + ","
                + getDescription();
    }
}