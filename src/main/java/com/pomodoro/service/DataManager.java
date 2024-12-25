package main.java.com.pomodoro.service;

import main.java.com.pomodoro.model.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DataManager {
    private static final String DATA_DIR = "pomodoro_data";
    private static final String TASKS_FILE = "tasks.json";
    private static final String SETTINGS_FILE = "settings.json";
    
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public List<Task> loadTasks() {
        try {
            String json = Files.readString(Path.of(DATA_DIR, TASKS_FILE));
            return JsonConverter.jsonToTasks(json);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
    
    public void saveSettings(Settings settings) {
        try {
            String json = JsonConverter.settingsToJson(settings);
            Files.writeString(Path.of(DATA_DIR, SETTINGS_FILE), json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Settings loadSettings() {
        try {
            String json = Files.readString(Path.of(DATA_DIR, SETTINGS_FILE));
            return JsonConverter.jsonToSettings(json);
        } catch (IOException e) {
            return new Settings();
        }
    }
}
