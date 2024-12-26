package com.pomodoro.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Tracks daily statistics for tasks and Pomodoro sessions.
 * Maintains counts of completed tasks and sessions for a specific date.
 */
public class DailyStats {
    private LocalDate date;
    private List<Task> tasks;
    private int completedTasksCount;
    private int totalPomodoroSessions;
    
    /**
     * Creates new daily statistics for specified date.
     *
     * @param date Date for these statistics
     */
    public DailyStats(LocalDate date) {
        this.date = date;
        this.tasks = new ArrayList<>();
        this.completedTasksCount = 0;
        this.totalPomodoroSessions = 0;
    }

    /**
     * Adds a task to daily statistics.
     * Updates completed task count if task is completed.
     *
     * @param task Task to add
     */
    public void addTask(Task task) {
        tasks.add(task);
        if (task.isCompleted()) {
            completedTasksCount++;
        }
    }

    /**
     * Increments the count of completed Pomodoro sessions.
     */
    public void incrementPomodoroSessions() {
        totalPomodoroSessions++;
    }

    /**
     * Marks a task as completed and updates statistics.
     *
     * @param task Task to mark as completed
     */
    public void taskCompleted(Task task) {
        if (!task.isCompleted()) {
            task.setCompleted(true);
            completedTasksCount++;
        }
    }

    // Getters
    /**
     * Gets the date for these statistics.
     *
     * @return Date for these statistics
     */
    public LocalDate getDate() { return date; }

    /**
     * Gets the list of tasks for these statistics.
     *
     * @return List of tasks
     */
    public List<Task> getTasks() { return new ArrayList<>(tasks); }

    /**
     * Gets the count of completed tasks.
     *
     * @return Count of completed tasks
     */
    public int getCompletedTasksCount() { return completedTasksCount; }

    /**
     * Gets the total number of Pomodoro sessions.
     *
     * @return Total number of Pomodoro sessions
     */
    public int getTotalPomodoroSessions() { return totalPomodoroSessions; }

    /**
     * Gets the total number of tasks.
     *
     * @return Total number of tasks
     */
    public int getTotalTasks() { return tasks.size(); }
}
