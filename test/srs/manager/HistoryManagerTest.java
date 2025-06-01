package srs.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import srs.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    HistoryManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    void shouldAddTaskToHistory() {
        Task task = new Task(1, "Test", "Description");
        manager.addToHistory(task);
        assertEquals(1, manager.getHistory().size(), "История должна содержать 1 задачу");
        assertEquals(task, manager.getHistory().getFirst(), "Добавленная задача должна быть в истории");
    }

    @Test
    void shouldRemoveOldestTaskWhenHistoryIsFull() {
        Task oldestTask = new Task(1, "Oldest", "Task");

        for (int i = 1; i <= InMemoryHistoryManager.MAX_HISTORY_SIZE; i++) {
            manager.addToHistory(new Task(i, "Task " + i, "Desc"));
        }
        Task newTask = new Task(99, "New", "Task");
        manager.addToHistory(newTask);

        List<Task> history = manager.getHistory();
        assertEquals(InMemoryHistoryManager.MAX_HISTORY_SIZE, history.size(), "Размер истории не должен превышать MAX_HISTORY_SIZE");
        assertFalse(history.contains(oldestTask), "Самая старая задача должна быть удалена");
        assertTrue(history.contains(newTask), "Новая задача должна быть в истории");
    }
}
