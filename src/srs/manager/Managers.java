package srs.manager;

import java.io.File;

import static srs.manager.FileBackedTaskManager.loadFromFile;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileBackedTM(File file) {
        return loadFromFile(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
