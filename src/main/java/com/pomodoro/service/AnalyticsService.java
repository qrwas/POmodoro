package com.pomodoro.service;

import com.pomodoro.model.*;
import java.util.*;

public class AnalyticsService {
    private final DataManager dataManager;
    private List<AnalyticsListener> listeners = new ArrayList<>();
    private Map<String, TaskStats> taskStats = new HashMap<>();
    private int totalPomodoros = 0;

    public AnalyticsService(DataManager dataManager) {
        this.dataManager = dataManager;
        loadAnalytics();
    }

    public interface AnalyticsListener {
        void onStatsUpdated(Map<String, TaskStats> stats, int totalPomodoros);
    }

    public void addListener(AnalyticsListener listener) {
        listeners.add(listener);
    }

    public void recordPomodoro(Task task, int duration) {
        TaskStats stats = taskStats.computeIfAbsent(
            task.getName(), 
            TaskStats::new
        );
        stats.addPomodoro(duration);
        totalPomodoros++;
        notifyListeners();
        saveAnalytics();
    }

    public Map<String, TaskStats> getTaskStats() {
        return new HashMap<>(taskStats);
    }

    public int getTotalPomodoros() {
        return totalPomodoros;
    }

    private void notifyListeners() {
        for (AnalyticsListener listener : listeners) {
            listener.onStatsUpdated(getTaskStats(), totalPomodoros);
        }
    }

    private void loadAnalytics() {
        AnalyticsData data = dataManager.loadAnalytics();
        this.taskStats = new HashMap<>(data.getTaskStats());
        this.totalPomodoros = data.getTotalPomodoros();
        notifyListeners();
    }

    public void saveAnalytics() {
        dataManager.saveAnalytics(taskStats, totalPomodoros);
    }
}
