package srs.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtaskIds;
    private LocalDateTime endTime;

    public Epic(int id, String name, String description) {
        super(id, Type.EPIC, name, description, Duration.ofMinutes(1), LocalDateTime.of(2025, 7, 18, 11,11));
        this.subtaskIds = new ArrayList<>();
        this.endTime = LocalDateTime.now().plus(Duration.ofMinutes(1));
    }

    public Epic(int id, String name, String description, Status status,  Duration duration, LocalDateTime startTime) {
        super(id, Type.EPIC, name, description, status, duration, startTime);
        this.subtaskIds = new ArrayList<>();
        this.endTime = startTime.plus(duration);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        if (subtaskId != getId()) {
            subtaskIds.add(subtaskId);
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }
}