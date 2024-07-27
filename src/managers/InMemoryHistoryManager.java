package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> historyMap = new HashMap<>();
    private Node first = null;
    private Node last = null;

    private void linkLast(Task task) {
        Node node = new Node(null, task, null);

        if (historyMap.isEmpty()) {
            last = node;
            first = node;
        } else {
            last.setNext(node);
            node.setPrev(last);
            last = node;
        }

        historyMap.put(task.getID(), node);
    }

    public List<Task> getTasks() {
        List<Task> historyList = new ArrayList<>();
        List<Integer> keys = new ArrayList<>(historyMap.keySet());

        Collections.reverse(keys);

        for (Integer key : keys) {
            historyList.add(historyMap.get(key).task);
        }

        return historyList;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        Node prev = node.getPrev();
        Node next = node.getNext();

        if (first == node) {
            first = node.getNext();
        } else {
            prev.setNext(next);
        }

        if (last == node) {
            last = node.getPrev();
        } else {
            next.setPrev(prev);
        }

        historyMap.remove(node.task.getID());
    }

    @Override
    public void add(Task task) {
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        removeNode(historyMap.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}