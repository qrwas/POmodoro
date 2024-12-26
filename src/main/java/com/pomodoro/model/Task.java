package com.pomodoro.model;

import java.time.LocalDateTime;

public class Task {
    private String name;
    private int priority;
    private boolean completed;
    private boolean inProgress;
    private int index; // Add index field
    private int plannedDuration; // in seconds
    private LocalDateTime completionTime; // Add completionTime field

    public Task(String name, int priority) {
        this.name = name;
        this.priority = priority;
        this.completed = false;
        this.inProgress = false;
        this.plannedDuration = 25 * 60; // default 25 minutes
        this.completionTime = null; // default null
    }

    // Add index getter/setter
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }
    
    // Add getter and setter for plannedDuration
    public int getPlannedDuration() { return plannedDuration; }
    public void setPlannedDuration(int duration) { this.plannedDuration = duration; }

    // Add getter and setter for completionTime
    public LocalDateTime getCompletionTime() { return completionTime; }
    public void setCompletionTime(LocalDateTime completionTime) { this.completionTime = completionTime; }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public boolean isInProgress() { return inProgress; }
    public void setInProgress(boolean inProgress) { this.inProgress = inProgress; }
}
