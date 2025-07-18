package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import srs.manager.HistoryManager;
import srs.manager.InMemoryHistoryManager;
import srs.model.Task;
import srs.model.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    private HistoryManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    void shouldAddTaskToHistory() {
        Task task = new Task(1, Type.TASK, "Test", "Description", Duration.ofMinutes(20), LocalDateTime.of(2025, 7, 17, 22, 12));
        manager.add(task);
        assertEquals(1, manager.getHistory().size(), "История должна содержать 1 задачу");
        assertTrue(manager.getHistory().contains(task), "Добавленная задача должна быть в истории");
    }

    @Test
    void removeShouldDeleteTaskFromHistory() {
        Task task1 = new Task(1, Type.TASK,"Task1", "Desc", Duration.ofMinutes(20), LocalDateTime.of(2025, 7, 17, 22, 12));
        Task task2 = new Task(2, Type.TASK,"Task2", "Desc", Duration.ofMinutes(30), LocalDateTime.of(2025, 8, 17, 22, 12));

        manager.add(task1);
        manager.add(task2);
        manager.remove(1);

        assertEquals(1, manager.getHistory().size());
        assertEquals(task2, manager.getHistory().getFirst());
    }

    @Test
    void getHistoryShouldMaintainInsertionOrder() {
        Task task1 = new Task(1, Type.TASK, "Task1", "Desc", Duration.ofMinutes(20), LocalDateTime.of(2025, 6, 17, 22, 12));
        Task task2 = new Task(2, Type.TASK,"Task2", "Desc", Duration.ofMinutes(30), LocalDateTime.of(2025, 7, 17, 22, 12));

        manager.add(task1);
        manager.add(task2);

        List<Task> history = manager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    void addShouldNotAcceptNullTask() {
        manager.add(null);
        assertTrue(manager.getHistory().isEmpty());
    }
}
