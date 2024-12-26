package com.pomodoro.model;

public class Task {
    private String name;
    private int priority;
    private boolean completed;
    private boolean inProgress;
    private int index; // Add index field
    private int plannedDuration; // in seconds

    public Task(String name, int priority) {
        this.name = name;
        this.priority = priority;
        this.completed = false;
        this.inProgress = false;
        this.plannedDuration = 25 * 60; // default 25 minutes
    }

    // Add index getter/setter
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }
    
    // Add getter and setter for plannedDuration
    public int getPlannedDuration() { return plannedDuration; }
    public void setPlannedDuration(int duration) { this.plannedDuration = duration; }

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
