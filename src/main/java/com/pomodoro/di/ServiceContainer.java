package com.pomodoro.di;

import com.pomodoro.model.Settings;
import com.pomodoro.service.*;

/**
 * Dependency injection container for the Pomodoro application.
 * Manages creation and lifecycle of all application services.
 */
public class ServiceContainer {
    private final Settings settings;
    private final TaskManager taskManager;
    private final DataManager dataManager;
    private final AnalyticsService analyticsService;

    /**
     * Creates a new ServiceContainer and initializes all application services.
     * Services are initialized in the correct dependency order.
     */
    public ServiceContainer() {
        this.dataManager = new DataManager();
        this.settings = dataManager.loadSettings();
        this.analyticsService = new AnalyticsService(dataManager);
        this.taskManager = new TaskManager(dataManager, analyticsService, settings);
    }

    /**
     * Gets the application settings.
     * @return Current application settings
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * Gets the task manager service.
     * @return Task manager instance
     */
    public TaskManager getTaskManager() {
        return taskManager;
    }

    /**
     * Gets the data persistence manager.
     * @return Data manager instance
     */
    public DataManager getDataManager() {
        return dataManager;
    }

    /**
     * Gets the analytics service.
     * @return Analytics service instance
     */
    public AnalyticsService getAnalyticsService() {
        return analyticsService;
    }

    /**
     * Saves all application data.
     * Persists settings, tasks, and analytics data.
     */
    public void saveAll() {
        System.out.println("settings " + settings.getWorkInterval());
        dataManager.saveSettings(settings);
        taskManager.saveTasks();
        analyticsService.saveAnalytics();
    }
}
