package com.pomodoro.service;

import com.pomodoro.model.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Manages analytics data for the Pomodoro application.
 * Tracks task statistics and total completed Pomodoros.
 */
public class AnalyticsService {
    private final DataManager dataManager;
    private List<AnalyticsListener> listeners = new ArrayList<>();
    private Map<String, TaskStats> taskStats = new HashMap<>();
    private int totalPomodoros = 0;

    /**
     * Creates a new AnalyticsService instance.
     *
     * @param dataManager Manager for persisting analytics data
     */
    public AnalyticsService(DataManager dataManager) {
        this.dataManager = dataManager;
        loadAnalytics();
    }

    /**
     * Interface for listening to analytics updates.
     */
    public interface AnalyticsListener {
        /**
         * Called when statistics are updated.
         *
         * @param stats Updated task statistics
         * @param totalPomodoros Total completed Pomodoros
         */
        void onStatsUpdated(Map<String, TaskStats> stats, int totalPomodoros);
    }

    /**
     * Adds an analytics update listener.
     *
     * @param listener Listener to add
     */
    public void addListener(AnalyticsListener listener) {
        listeners.add(listener);
    }

    /**
     * Records completion of a Pomodoro session.
     *
     * @param task Completed task
     * @param duration Duration of the Pomodoro
     */
    public void recordPomodoro(Task task, int duration) {
        TaskStats stats = taskStats.computeIfAbsent(
            task.getName(), 
            TaskStats::new
        );
        stats.addPomodoro(duration, task.getCompletionTime());
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
