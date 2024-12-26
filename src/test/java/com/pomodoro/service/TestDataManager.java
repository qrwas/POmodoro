package com.pomodoro.service;

import com.pomodoro.model.Task;
import com.pomodoro.model.Settings;
import java.util.ArrayList;
import java.util.List;

/**
 * Test implementation of DataManager for unit testing.
 * Provides in-memory storage of tasks and settings.
 */
public class TestDataManager extends DataManager {
    private List<Task> tasks = new ArrayList<>();
    private Settings settings = new Settings();

    /**
     * Creates a new TestDataManager instance.
     */
    public TestDataManager() {
        super(); // null for test purposes
    }

    /**
     * Returns a copy of in-memory tasks list.
     * @return List of tasks
     */
    @Override
    public List<Task> loadTasks() {
        return new ArrayList<>(tasks);
    }

    /**
     * Stores tasks in memory.
     * @param tasks Tasks to store
     */
    @Override
    public void saveTasks(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the in-memory settings.
     * @return Settings
     */
    @Override
    public Settings loadSettings() {
        return settings;
    }

    /**
     * Stores settings in memory.
     * @param settings Settings to store
     */
    @Override
    public void saveSettings(Settings settings) {
        this.settings = settings;
    }
}
