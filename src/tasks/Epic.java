package tasks;

import java.util.ArrayList;

public class Epic extends Task {

    public Epic(String name, String description) {
        super(name, description);
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
        return "tasks.Epic{" +
                "name= " + getName() + '\'' +
                ", description = " + getDescription() + '\'' +
                ", id=" + getID() +
                ", subtaskList.size = " + subtaskList.size() +
                ", status = " + getStatus() +
                '}';
    }
}