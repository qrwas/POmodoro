package com.pomodoro.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Stores statistics for a single task in the Pomodoro application.
 * Tracks total time spent and completion time for the task.
 */
public class TaskStats {
    private final String taskName;
    private long totalTimeSpent; // in seconds
    private LocalDateTime completionTime;

    /**
     * Creates new statistics for a task.
     *
     * @param taskName Name of the task to track
     */
    public TaskStats(String taskName) {
        this.taskName = taskName;
        this.totalTimeSpent = 0;
        this.completionTime = null;
    }

    /**
     * Records completion of a Pomodoro session for this task.
     *
     * @param duration Duration of the Pomodoro in seconds
     * @param completionTime Time when the Pomodoro was completed
     */
    public void addPomodoro(int duration, LocalDateTime completionTime) {
        totalTimeSpent += duration;
        this.completionTime = completionTime;
    }

    /** @return Name of the task */
    public String getTaskName() { return taskName; }
    
    /** @return Total time spent on the task in seconds */
    public long getTotalTimeSpent() { return totalTimeSpent; }
    
    /**
     * Returns formatted string of total time spent.
     * @return Time in HH:MM format
     */
    public String getFormattedTimeSpent() {
        long hours = totalTimeSpent / 3600;
        long minutes = (totalTimeSpent % 3600) / 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    /**
     * Sets the total time spent on the task.
     * @param totalTimeSpent Time in seconds
     */
    public void setTotalTimeSpent(long totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }

    /** @return Time when the last Pomodoro was completed */
    public LocalDateTime getCompletionTime() {
        return completionTime;
    }

    /**
     * Sets the completion time of the last Pomodoro.
     * @param completionTime Completion time to set
     */
    public void setCompletionTime(LocalDateTime completionTime) {
        this.completionTime = completionTime;
    }

    /**
     * Converts task statistics to a map for serialization.
     * @return Map containing task statistics data
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("taskName", taskName);
        map.put("totalTimeSpent", totalTimeSpent);
        map.put("completionTime", completionTime != null ? completionTime.toString() : null);
        return map;
    }

    /**
     * Creates TaskStats instance from a map.
     *
     * @param map Map containing task statistics data
     * @return New TaskStats instance
     */
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
