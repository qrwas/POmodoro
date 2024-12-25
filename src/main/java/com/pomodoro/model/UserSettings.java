package com.pomodoro.model;

public class UserSettings {
    private int workDuration;
    private int shortBreakDuration;
    private int longBreakDuration;
    private int sessionsUntilLongBreak;
    private boolean darkTheme;
    private boolean soundEnabled;

    public UserSettings() {
        // Default settings
        this.workDuration = 25;
        this.shortBreakDuration = 5;
        this.longBreakDuration = 15;
        this.sessionsUntilLongBreak = 4;
        this.darkTheme = false;
        this.soundEnabled = true;
    }

    // Getters and setters
    // ... Standard getters and setters for all fields ...
}
