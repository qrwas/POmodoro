package main.java.com.pomodoro.di;

import main.java.com.pomodoro.model.Settings;
import main.java.com.pomodoro.service.TaskManager;
import main.java.com.pomodoro.service.DataManager;

public class ServiceContainer {
    private final Settings settings;
    private final TaskManager taskManager;
    private final DataManager dataManager;

    public ServiceContainer() {
        this.dataManager = new DataManager();
        this.settings = dataManager.loadSettings();
        this.taskManager = new TaskManager(dataManager);
    }

    public Settings getSettings() {
        return settings;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public void saveAll() {
        dataManager.saveSettings(settings);
        taskManager.saveTasks();
    }
}
