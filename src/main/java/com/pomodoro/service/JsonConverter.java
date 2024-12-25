package main.java.com.pomodoro.service;

import main.java.com.pomodoro.model.*;
import java.util.*;

public class JsonConverter {
    public static String tasksToJson(List<Task> tasks) {
        StringBuilder json = new StringBuilder("{\n  \"tasks\": [\n");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            json.append("    {\n")
                .append("      \"name\": \"").append(escapeJson(task.getName())).append("\",\n")
                .append("      \"priority\": ").append(task.getPriority()).append(",\n")
                .append("      \"completed\": ").append(task.isCompleted()).append(",\n")
                .append("      \"index\": ").append(task.getIndex()).append("\n")
                .append("    }").append(i < tasks.size() - 1 ? ",\n" : "\n");
        }
        json.append("  ]\n}");
        return json.toString();
    }

    public static List<Task> jsonToTasks(String json) {
        List<Task> tasks = new ArrayList<>();
        if (json == null || json.isEmpty()) {
            return tasks;
        }

        // Simple JSON parsing
        String[] taskObjects = json.split("\\{\\s*\"name\":");
        for (int i = 1; i < taskObjects.length; i++) { // Skip first split as it's the header
            String taskJson = taskObjects[i];
            String name = extractString(taskJson, "\"name\":");
            int priority = extractInt(taskJson, "\"priority\":");
            boolean completed = extractBoolean(taskJson, "\"completed\":");
            int index = extractInt(taskJson, "\"index\":");

            Task task = new Task(name, priority);
            task.setCompleted(completed);
            task.setIndex(index);
            tasks.add(task);
        }
        return tasks;
    }

    public static String settingsToJson(Settings settings) {
        return "{\n" +
               "  \"workInterval\": " + settings.getWorkInterval() + ",\n" +
               "  \"shortBreakInterval\": " + settings.getShortBreakInterval() + ",\n" +
               "  \"longBreakInterval\": " + settings.getLongBreakInterval() + ",\n" +
               "  \"sessionsUntilLongBreak\": " + settings.getSessionsUntilLongBreak() + "\n" +
               "}";
    }

    public static Settings jsonToSettings(String json) {
        if (json == null || json.isEmpty()) {
            return new Settings();
        }

        return new Settings(
            extractInt(json, "\"workInterval\":"),
            extractInt(json, "\"shortBreakInterval\":"),
            extractInt(json, "\"longBreakInterval\":"),
            extractInt(json, "\"sessionsUntilLongBreak\":")
        );
    }

    private static String escapeJson(String text) {
        return text.replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    private static String extractString(String json, String key) {
        int start = json.indexOf(key) + key.length();
        start = json.indexOf("\"", start) + 1;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }

    private static int extractInt(String json, String key) {
        int start = json.indexOf(key) + key.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        return Integer.parseInt(json.substring(start, end).trim());
    }

    private static boolean extractBoolean(String json, String key) {
        int start = json.indexOf(key) + key.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        return Boolean.parseBoolean(json.substring(start, end).trim());
    }
}
