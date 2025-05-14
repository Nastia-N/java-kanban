package srs.model;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(int id, String name, String description, int epicId) {
        super(id, name, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    } //Subtask использует equals() и hashCode() от Task, так как сравниваем только по id (а он уникален для каждой задачи)
}