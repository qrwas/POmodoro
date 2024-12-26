package com.pomodoro.service;

import com.pomodoro.model.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DataManager {
    private static final String DATA_DIR = "pomodoro_data";
    private static final String TASKS_FILE = "tasks.json";
    private static final String SETTINGS_FILE = "settings.json";
    private static final String ANALYTICS_FILE = "analytics.json";
    
    public DataManager() {
        createDataDirectory();
    }
    
    private void createDataDirectory() {
        try {
            Files.createDirectories(Path.of(DATA_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void saveTasks(List<Task> tasks) {
        try {
            String json = JsonConverter.tasksToJson(tasks);
            Files.writeString(Path.of(DATA_DIR, TASKS_FILE), json);
            System.out.println("Tasks saved: " + json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public List<Task> loadTasks() {
        try {
            String json = Files.readString(Path.of(DATA_DIR, TASKS_FILE));
            System.out.println("Tasks loaded: " + json);
            return JsonConverter.jsonToTasks(json);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
    
    public void saveSettings(Settings settings) {
        try {
            String json = JsonConverter.settingsToJson(settings);
            Files.writeString(Path.of(DATA_DIR, SETTINGS_FILE), json);
            System.out.println("Settings saved: " + json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Settings loadSettings() {
        try {
            String json = Files.readString(Path.of(DATA_DIR, SETTINGS_FILE));
            System.out.println("Settings loaded: " + json);
            return JsonConverter.jsonToSettings(json);
        } catch (IOException e) {
            return new Settings();
        }
    }

    public void saveAnalytics(Map<String, TaskStats> taskStats, int totalPomodoros) {
        try {
            String json = JsonConverter.analyticsToJson(taskStats, totalPomodoros);
            Files.writeString(Path.of(DATA_DIR, ANALYTICS_FILE), json);
            System.out.println("Analytics saved: " + json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public AnalyticsData loadAnalytics() {
        try {
            String json = Files.readString(Path.of(DATA_DIR, ANALYTICS_FILE));
            System.out.println("Analytics loaded: " + json);
            return JsonConverter.jsonToAnalytics(json);
        } catch (IOException e) {
            return new AnalyticsData(new HashMap<>(), 0);
        }
    }
}
