package com.pomodoro.model;

import java.util.Map;

/**
 * Container for analytics data including task statistics and total Pomodoro count.
 */
public class AnalyticsData {
    private final Map<String, TaskStats> taskStats;
    private final int totalPomodoros;

    /**
     * Creates new analytics data container.
     *
     * @param taskStats Map of task names to their statistics
     * @param totalPomodoros Total number of completed Pomodoros
     */
    public AnalyticsData(Map<String, TaskStats> taskStats, int totalPomodoros) {
        this.taskStats = taskStats;
        this.totalPomodoros = totalPomodoros;
    }

    /**
     * Gets the map of task statistics.
     *
     * @return Map of task names to their statistics
     */
    public Map<String, TaskStats> getTaskStats() {
        return taskStats;
    }

    /**
     * Gets the total number of completed Pomodoros.
     *
     * @return Total Pomodoro count
     */
    public int getTotalPomodoros() {
        return totalPomodoros;
    }
}
