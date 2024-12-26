package com.pomodoro.service;

import com.google.gson.*;
import com.pomodoro.model.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Handles JSON serialization and deserialization for application data.
 * Provides conversion methods for tasks, settings, and analytics data.
 */
public class JsonConverter {
    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .create();

    /**
     * Converts a list of tasks to JSON string.
     *
     * @param tasks List of tasks to convert
     * @return JSON string representation
     */
    public static String tasksToJson(List<Task> tasks) {
        return gson.toJson(tasks);
    }

    /**
     * Converts JSON string to list of tasks.
     *
     * @param json JSON string to convert
     * @return List of Task objects
     */
    public static List<Task> jsonToTasks(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        Type listType = new com.google.gson.reflect.TypeToken<List<Task>>(){}.getType();
        return gson.fromJson(json, listType);
    }

    /**
     * Converts settings object to JSON string.
     *
     * @param settings Settings to convert
     * @return JSON string representation
     */
    public static String settingsToJson(Settings settings) {
        return gson.toJson(settings);
    }

    public static Settings jsonToSettings(String json) {
        if (json == null || json.isEmpty()) {
            return new Settings();
        }
        return gson.fromJson(json, Settings.class);
    }

    public static String analyticsToJson(Map<String, TaskStats> taskStats, int totalPomodoros) {
        Map<String, Object> data = new HashMap<>();
        data.put("taskStats", taskStats);
        data.put("totalPomodoros", totalPomodoros);
        return gson.toJson(data);
    }

    public static AnalyticsData jsonToAnalytics(String json) {
        if (json == null || json.isEmpty()) {
            return new AnalyticsData(new HashMap<>(), 0);
        }
        Type type = new com.google.gson.reflect.TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> data = gson.fromJson(json, type);
        
        Map<String, TaskStats> taskStats = new HashMap<>();
        if (data.get("taskStats") != null) {
            Type statsType = new com.google.gson.reflect.TypeToken<Map<String, TaskStats>>(){}.getType();
            taskStats = gson.fromJson(gson.toJson(data.get("taskStats")), statsType);
        }
        
        int totalPomodoros = ((Double) data.get("totalPomodoros")).intValue();
        return new AnalyticsData(taskStats, totalPomodoros);
    }
}

/**
 * Handles JSON serialization/deserialization of LocalDateTime objects.
 */
class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return LocalDateTime.parse(json.getAsString());
    }
}
