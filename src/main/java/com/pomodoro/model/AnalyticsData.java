package com.pomodoro.model;

import java.util.Map;

public class AnalyticsData {
    private final Map<String, TaskStats> taskStats;
    private final int totalPomodoros;

    public AnalyticsData(Map<String, TaskStats> taskStats, int totalPomodoros) {
        this.taskStats = taskStats;
        this.totalPomodoros = totalPomodoros;
    }

    public Map<String, TaskStats> getTaskStats() {
        return taskStats;
    }

    public int getTotalPomodoros() {
        return totalPomodoros;
    }
}
