package main.java.com.pomodoro.service;

import main.java.com.pomodoro.model.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DataManager {
    private static final String DATA_DIR = "pomodoro_data";
    private static final String TASKS_FILE = "tasks.properties";
    private static final String SETTINGS_FILE = "settings.properties";
    
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
        Properties props = new Properties();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String prefix = "task." + i + ".";
            props.setProperty(prefix + "name", task.getName());
            props.setProperty(prefix + "priority", String.valueOf(task.getPriority()));
            props.setProperty(prefix + "completed", String.valueOf(task.isCompleted()));
        }
        props.setProperty("tasks.count", String.valueOf(tasks.size()));
        
        try (OutputStream out = Files.newOutputStream(Path.of(DATA_DIR, TASKS_FILE))) {
            props.store(out, "Pomodoro Tasks");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public List<Task> loadTasks() {
        Properties props = new Properties();
        List<Task> tasks = new ArrayList<>();
        
        try (InputStream in = Files.newInputStream(Path.of(DATA_DIR, TASKS_FILE))) {
            props.load(in);
            int count = Integer.parseInt(props.getProperty("tasks.count", "0"));
            
            for (int i = 0; i < count; i++) {
                String prefix = "task." + i + ".";
                String name = props.getProperty(prefix + "name");
                int priority = Integer.parseInt(props.getProperty(prefix + "priority"));
                boolean completed = Boolean.parseBoolean(props.getProperty(prefix + "completed"));
                
                Task task = new Task(name, priority);
                task.setCompleted(completed);
                task.setIndex(i);
                tasks.add(task);
            }
        } catch (IOException e) {
            // Return empty list if file doesn't exist
            return tasks;
        }
        
        return tasks;
    }
    
    public void saveSettings(Settings settings) {
        Properties props = new Properties();
        props.setProperty("workInterval", String.valueOf(settings.getWorkInterval()));
        props.setProperty("shortBreakInterval", String.valueOf(settings.getShortBreakInterval()));
        props.setProperty("longBreakInterval", String.valueOf(settings.getLongBreakInterval()));
        props.setProperty("sessionsUntilLongBreak", String.valueOf(settings.getSessionsUntilLongBreak()));
        
        try (OutputStream out = Files.newOutputStream(Path.of(DATA_DIR, SETTINGS_FILE))) {
            props.store(out, "Pomodoro Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Settings loadSettings() {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(Path.of(DATA_DIR, SETTINGS_FILE))) {
            props.load(in);
            return new Settings(
                Integer.parseInt(props.getProperty("workInterval", "1500")),
                Integer.parseInt(props.getProperty("shortBreakInterval", "300")),
                Integer.parseInt(props.getProperty("longBreakInterval", "900")),
                Integer.parseInt(props.getProperty("sessionsUntilLongBreak", "4"))
            );
        } catch (IOException e) {
            return new Settings(); // Return default settings if file doesn't exist
        }
    }
}
