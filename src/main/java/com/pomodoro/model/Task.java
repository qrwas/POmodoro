package com.pomodoro.model;

import java.time.LocalDateTime;

public class Task {
    private String id;
    private String name;
    private Priority priority;
    private boolean completed;
    private LocalDateTime createdAt;
    private int completedPomodoros;

    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    public Task(String name, Priority priority) {
        this.id = java.util.UUID.randomUUID().toString();
        this.name = name;
        this.priority = priority;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
        this.completedPomodoros = 0;
    }

    // Getters and setters
    // ... Standard getters and setters for all fields ...
}
