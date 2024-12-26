package com.pomodoro.service;

import com.pomodoro.model.Task;
import com.pomodoro.model.Settings;
import java.util.ArrayList;
import java.util.List;

public class TestDataManager extends DataManager {
    private List<Task> tasks = new ArrayList<>();
    private Settings settings = new Settings();

    public TestDataManager() {
        super(); // null for test purposes
    }

    @Override
    public List<Task> loadTasks() {
        return new ArrayList<>(tasks);
    }

    @Override
    public void saveTasks(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    @Override
    public Settings loadSettings() {
        return settings;
    }

    @Override
    public void saveSettings(Settings settings) {
        this.settings = settings;
    }
}
