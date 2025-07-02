package srs.manager;

import srs.model.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;

    private void removeNode(Node node) {
        if (node == null) return;

        if (node.getPrev() != null) {
            node.getPrev().setNext(node.getNext());
        } else {
            head = node.getNext();
        }

        if (node.getNext() != null) {
            node.getNext().setPrev(node.getPrev());
        } else {
            tail = node.getPrev();
        }
    }

    private void linkLast(Task task) {
        final Node newNode = new Node(task, tail, null);

        if (tail == null) {
            head = newNode;
        } else {
            tail.setNext(newNode);
        }

        tail = newNode;
    }

    private List<Task> getTasks() {
        List<Task> result = new ArrayList<>();
        Node current = head;
        while (current != null) {
            result.add(current.getTask());
            current = current.getNext();
        }
        return result;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        Node existingNode = nodeMap.get(task.getId());
        if (existingNode != null) {
            removeNode(existingNode);
        }
        linkLast(task);
        nodeMap.put(task.getId(), tail);
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.get(id);
        if (node != null) {
            removeNode(node);
            nodeMap.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}