package srs.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtaskIds;

    public Epic(int id, String name, String description) {
        super(id, name, description);
        this.subtaskIds = new ArrayList<>();
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    } //Epic использует equals() и hashCode() от Task, так как сравниваем только по id (а он уникален для каждой задачи)
}