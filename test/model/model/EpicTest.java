package model.model;

import org.junit.jupiter.api.Test;
import srs.model.Epic;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EpicTest {
    @Test
    void epicCannotBeSubtaskOfItself() {
        Epic epic = new Epic(5, "Epic", "Desc");
        epic.addSubtaskId(epic.getId());
        assertTrue(epic.getSubtaskIds().isEmpty(), "Epic нельзя добавить в самого себя в виде подзадачи");
    }
}
