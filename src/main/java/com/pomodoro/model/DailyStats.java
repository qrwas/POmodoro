package com.pomodoro.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyStats {
    private LocalDate date;
    private List<Task> tasks;
    private int completedTasksCount;
    private int totalPomodoroSessions;
    
    public DailyStats(LocalDate date) {
        this.date = date;
        this.tasks = new ArrayList<>();
        this.completedTasksCount = 0;
        this.totalPomodoroSessions = 0;
    }

    public void addTask(Task task) {
        tasks.add(task);
        if (task.isCompleted()) {
            completedTasksCount++;
        }
    }

    public void incrementPomodoroSessions() {
        totalPomodoroSessions++;
    }

    public void taskCompleted(Task task) {
        if (!task.isCompleted()) {
            task.setCompleted(true);
            completedTasksCount++;
        }
    }

    // Getters
    public LocalDate getDate() { return date; }
    public List<Task> getTasks() { return new ArrayList<>(tasks); }
    public int getCompletedTasksCount() { return completedTasksCount; }
    public int getTotalPomodoroSessions() { return totalPomodoroSessions; }
    public int getTotalTasks() { return tasks.size(); }
}
