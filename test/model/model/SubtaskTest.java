package model.model;

import org.junit.jupiter.api.Test;
import srs.model.Subtask;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTest {
    @Test
    void subtasksWithSameIdShouldBeEqual() {
        Subtask subtask1 = new Subtask(1, "Sub A", "Desc", 6);
        Subtask subtask2 = new Subtask(1, "Sub A", "Desc", 6);
        assertEquals(subtask1, subtask2, "Подзадачи с одинаковым id должны быть равны");
    }
}
