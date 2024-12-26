package com.pomodoro.model;

import java.time.LocalDateTime;

/**
 * Represents a task in the Pomodoro application.
 * Contains task properties like name, priority, completion status, and timing information.
 */
public class Task {
    private String name;
    private int priority;
    private boolean completed;
    private boolean inProgress;
    private int index; // Add index field
    private int plannedDuration; // in seconds
    private LocalDateTime completionTime; // Add completionTime field

    /**
     * Creates a new task with specified name and priority.
     *
     * @param name Task name
     * @param priority Task priority level
     */
    public Task(String name, int priority) {
        this.name = name;
        this.priority = priority;
        this.completed = false;
        this.inProgress = false;
        this.plannedDuration = 25 * 60; // default 25 minutes
        this.completionTime = null; // default null
    }

    /**
     * Gets the task's position index in the list.
     * @return Task index
     */
    public int getIndex() { return index; }
    
    /**
     * Sets the task's position index.
     * @param index New index value
     */
    public void setIndex(int index) { this.index = index; }
    
    /**
     * Gets the planned duration for this task in seconds.
     * @return Planned duration in seconds
     */
    public int getPlannedDuration() { return plannedDuration; }
    
    /**
     * Sets the planned duration for this task.
     * @param duration Duration in seconds
     */
    public void setPlannedDuration(int duration) { this.plannedDuration = duration; }

    /**
     * Gets the completion time for this task.
     * @return Completion time
     */
    public LocalDateTime getCompletionTime() { return completionTime; }
    
    /**
     * Sets the completion time for this task.
     * @param completionTime Completion time
     */
    public void setCompletionTime(LocalDateTime completionTime) { this.completionTime = completionTime; }

    /**
     * Gets the task name.
     * @return Task name
     */
    public String getName() { return name; }
    
    /**
     * Sets the task name.
     * @param name Task name
     */
    public void setName(String name) { this.name = name; }
    
    /**
     * Gets the task priority.
     * @return Task priority
     */
    public int getPriority() { return priority; }
    
    /**
     * Sets the task priority.
     * @param priority Task priority
     */
    public void setPriority(int priority) { this.priority = priority; }
    
    /**
     * Checks if the task is completed.
     * @return True if completed, false otherwise
     */
    public boolean isCompleted() { return completed; }
    
    /**
     * Sets the task completion status.
     * @param completed Completion status
     */
    public void setCompleted(boolean completed) { this.completed = completed; }
    
    /**
     * Checks if the task is in progress.
     * @return True if in progress, false otherwise
     */
    public boolean isInProgress() { return inProgress; }
    
    /**
     * Sets the task progress status.
     * @param inProgress Progress status
     */
    public void setInProgress(boolean inProgress) { this.inProgress = inProgress; }
}
