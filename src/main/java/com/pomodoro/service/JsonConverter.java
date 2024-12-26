package com.pomodoro.service;

import com.pomodoro.model.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonConverter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static String tasksToJson(List<Task> tasks) {
        StringBuilder json = new StringBuilder("{\n  \"tasks\": [\n");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("Task: " + tasks.get(i).getName());
            Task task = tasks.get(i);
            json.append("    {\n")
                .append("      \"name\": \"").append(escapeJson(task.getName())).append("\",\n")
                .append("      \"priority\": ").append(task.getPriority()).append(",\n")
                .append("      \"completed\": ").append(task.isCompleted()).append(",\n")
                .append("      \"index\": ").append(task.getIndex()).append(",\n")
                .append("      \"plannedDuration\": ").append(task.getPlannedDuration()).append(",\n")
                .append("      \"completionTime\": \"").append(task.getCompletionTime() != null ? task.getCompletionTime().format(formatter) : "").append("\"\n")
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
            String taskJson = "name:\n" + taskObjects[i];
            Task task = parseTask(taskJson);
            tasks.add(task);
        }
        return tasks;
    }

    private static Task parseTask(String json) {
        String name = extractString(json, "\"name\":");
        System.out.println("Task name: " + name);
        System.out.println("Task json: " + json);
        int priority = extractInt(json, "\"priority\":");
        boolean completed = extractBoolean(json, "\"completed\":");
        int index = extractInt(json, "\"index\":");
        int plannedDuration = extractInt(json, "\"plannedDuration\":");
        String completionTimeStr = extractString(json, "\"completionTime\":");
        LocalDateTime completionTime = completionTimeStr.isEmpty() ? null : LocalDateTime.parse(completionTimeStr, formatter);

        Task task = new Task(name, priority);
        task.setCompleted(completed);
        task.setIndex(index);
        task.setPlannedDuration(plannedDuration);
        task.setCompletionTime(completionTime);
        return task;
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
        // if (json == null || json.isEmpty()) {

        //     return new Settings();
        // }

            return new Settings(
                extractInt(json, "\"workInterval\":"),
                extractInt(json, "\"shortBreakInterval\":"),
                extractInt(json, "\"longBreakInterval\":"),
                extractInt(json, "\"sessionsUntilLongBreak\":")
            );
    }

    public static String analyticsToJson(Map<String, TaskStats> taskStats, int totalPomodoros) {
        StringBuilder json = new StringBuilder("{\n");
        json.append("  \"totalPomodoros\": ").append(totalPomodoros).append(",\n");
        json.append("  \"taskStats\": {\n");
        
        int i = 0;
        for (Map.Entry<String, TaskStats> entry : taskStats.entrySet()) {
            TaskStats stats = entry.getValue();
            json.append("    \"").append(escapeJson(entry.getKey())).append("\": {\n");
            json.append("      \"totalTimeSpent\": ").append(stats.getTotalTimeSpent()).append("\n");
            json.append("    }").append(i < taskStats.size() - 1 ? ",\n" : "\n");
            i++;
        }
        json.append("  }\n}");
        return json.toString();
    }

    public static AnalyticsData jsonToAnalytics(String json) {
        if (json == null || json.isEmpty()) {
            return new AnalyticsData(new HashMap<>(), 0);
        }

        try {
            int totalPomodoros = extractInt(json, "\"totalPomodoros\":");
            Map<String, TaskStats> taskStats = new HashMap<>();
            
            int statsStart = json.indexOf("\"taskStats\":");
            if (statsStart != -1) {
                String statsJson = json.substring(statsStart);
                for (String taskName : extractTaskNames(statsJson)) {
                    TaskStats stats = new TaskStats(taskName);
                    String taskJson = extractTaskStatsJson(statsJson, taskName);
                    stats.setTotalTimeSpent(extractInt(taskJson, "\"totalTimeSpent\":"));
                    taskStats.put(taskName, stats);
                }
            }

            return new AnalyticsData(taskStats, totalPomodoros);
        } catch (Exception e) {
            return new AnalyticsData(new HashMap<>(), 0);
        }
    }

    private static List<String> extractTaskNames(String json) {
        List<String> names = new ArrayList<>();
        int start = json.indexOf('{', json.indexOf("taskStats")) + 1;
        int end = json.lastIndexOf('}');
        String statsContent = json.substring(start, end);
        
        Pattern pattern = Pattern.compile("\"([^\"]+)\":\\s*\\{");
        Matcher matcher = pattern.matcher(statsContent);
        while (matcher.find()) {
            names.add(matcher.group(1));
        }
        return names;
    }

    private static String extractTaskStatsJson(String json, String taskName) {
        int start = json.indexOf("\"" + taskName + "\":");
        if (start == -1) return "{}";
        start = json.indexOf('{', start);
        int end = json.indexOf('}', start) + 1;
        return json.substring(start, end);
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
        String value = json.substring(start, end).trim();
        value = value.replace("\"", "");
        return Integer.parseInt(value);
    }

    private static boolean extractBoolean(String json, String key) {
        int start = json.indexOf(key) + key.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        String value = json.substring(start, end).trim();
        value = value.replace("\"", "");
        return Boolean.parseBoolean(value);
    }
}
