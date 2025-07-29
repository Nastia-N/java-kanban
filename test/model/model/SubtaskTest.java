package model.model;

import org.junit.jupiter.api.Test;
import srs.model.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTest {
    @Test
    void subtasksWithSameIdShouldBeEqual() {
        Subtask subtask1 = new Subtask(1, "Sub A", "Desc", 6, Duration.ofMinutes(10), LocalDateTime.of(2025,7,12,12,12));
        Subtask subtask2 = new Subtask(1, "Sub A", "Desc", 6, Duration.ofMinutes(10), LocalDateTime.of(2025,7,12,12,12));
        assertEquals(subtask1, subtask2, "Подзадачи с одинаковым id должны быть равны");
    }
}
