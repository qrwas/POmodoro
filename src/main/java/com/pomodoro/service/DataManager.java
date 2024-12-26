package com.pomodoro.service;

import com.pomodoro.model.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Manages persistence of application data.
 * Handles saving and loading of tasks, settings, and analytics data to/from files.
 */
public class DataManager {
    private static final String DATA_DIR = "pomodoro_data";
    private static final String TASKS_FILE = "tasks.json";
    private static final String SETTINGS_FILE = "settings.json";
    private static final String ANALYTICS_FILE = "analytics.json";
    
    /**
     * Creates a new DataManager and ensures data directory exists.
     */
    public DataManager() {
        createDataDirectory();
    }
    
    /**
     * Creates the data directory if it doesn't exist.
     */
    private void createDataDirectory() {
        try {
            Files.createDirectories(Path.of(DATA_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Saves tasks list to file.
     *
     * @param tasks List of tasks to save
     */
    public void saveTasks(List<Task> tasks) {
        try {
            String json = JsonConverter.tasksToJson(tasks);
            Files.writeString(Path.of(DATA_DIR, TASKS_FILE), json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Loads tasks from file.
     *
     * @return List of loaded tasks, or empty list if file doesn't exist
     */
    public List<Task> loadTasks() {
        try {
            String json = Files.readString(Path.of(DATA_DIR, TASKS_FILE));
            return JsonConverter.jsonToTasks(json);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Saves settings to file.
     *
     * @param settings Settings to save
     */
    public void saveSettings(Settings settings) {
        try {
            String json = JsonConverter.settingsToJson(settings);
            Files.writeString(Path.of(DATA_DIR, SETTINGS_FILE), json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Loads settings from file.
     *
     * @return Loaded settings, or new settings if file doesn't exist
     */
    public Settings loadSettings() {
        try {
            String json = Files.readString(Path.of(DATA_DIR, SETTINGS_FILE));
            return JsonConverter.jsonToSettings(json);
        } catch (IOException e) {
            System.out.println("Settings not found, creating new settings" + e);
            e.printStackTrace();
            return new Settings();
        }
    }

    /**
     * Saves analytics data to file.
     *
     * @param taskStats Map of task statistics to save
     * @param totalPomodoros Total number of pomodoros to save
     */
    public void saveAnalytics(Map<String, TaskStats> taskStats, int totalPomodoros) {
        try {
            String json = JsonConverter.analyticsToJson(taskStats, totalPomodoros);
            Files.writeString(Path.of(DATA_DIR, ANALYTICS_FILE), json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Loads analytics data from file.
     *
     * @return Loaded analytics data, or new analytics data if file doesn't exist
     */
    public AnalyticsData loadAnalytics() {
        try {
            String json = Files.readString(Path.of(DATA_DIR, ANALYTICS_FILE));
            return JsonConverter.jsonToAnalytics(json);
        } catch (IOException e) {
            return new AnalyticsData(new HashMap<>(), 0);
        }
    }
}
