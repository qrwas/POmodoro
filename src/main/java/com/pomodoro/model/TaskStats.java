package com.pomodoro.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

public class TaskStats {
    private final String taskName;
    private long totalTimeSpent; // in seconds
    private LocalDateTime completionTime;

    public TaskStats(String taskName) {
        this.taskName = taskName;
        this.totalTimeSpent = 0;
        this.completionTime = null;
    }

    public void addPomodoro(int duration, LocalDateTime completionTime) {
        totalTimeSpent += duration;
        this.completionTime = completionTime;
    }

    public String getTaskName() { return taskName; }
    public long getTotalTimeSpent() { return totalTimeSpent; }
    
    public String getFormattedTimeSpent() {
        long hours = totalTimeSpent / 3600;
        long minutes = (totalTimeSpent % 3600) / 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    public void setTotalTimeSpent(long totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }

    public LocalDateTime getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(LocalDateTime completionTime) {
        this.completionTime = completionTime;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("taskName", taskName);
        map.put("totalTimeSpent", totalTimeSpent);
        map.put("completionTime", completionTime != null ? completionTime.toString() : null);
        return map;
    }

    public static TaskStats fromMap(Map<String, Object> map) {
        TaskStats stats = new TaskStats((String) map.get("taskName"));
        stats.setTotalTimeSpent((Long) map.get("totalTimeSpent"));
        String completionTimeStr = (String) map.get("completionTime");
        if (completionTimeStr != null) {
            stats.setCompletionTime(LocalDateTime.parse(completionTimeStr));
        }
        return stats;
    }
}
