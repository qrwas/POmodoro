package main.java.com.pomodoro.di;

import main.java.com.pomodoro.model.Settings;
import main.java.com.pomodoro.service.TaskManager;
import main.java.com.pomodoro.service.DataManager;
import main.java.com.pomodoro.service.AnalyticsService;

public class ServiceContainer {
    private final Settings settings;
    private final TaskManager taskManager;
    private final DataManager dataManager;
    private final AnalyticsService analyticsService;

    public ServiceContainer() {
        this.dataManager = new DataManager();
        this.settings = dataManager.loadSettings();
        this.analyticsService = new AnalyticsService(dataManager);
        this.taskManager = new TaskManager(dataManager, analyticsService);
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

    public AnalyticsService getAnalyticsService() {
        return analyticsService;
    }

    public void saveAll() {
        dataManager.saveSettings(settings);
        taskManager.saveTasks();
        analyticsService.saveAnalytics();
    }
}
