package srs.manager;

import srs.model.Task;
import java.util.List;

public interface HistoryManager {
    void addToHistory(Task task);
    List<Task> getHistory();
}
