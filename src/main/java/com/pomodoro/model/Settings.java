package com.pomodoro.model;

/**
 * Stores application settings for Pomodoro timer intervals and session counts.
 */
public class Settings {
    private int workInterval;
    private int shortBreakInterval;
    private int longBreakInterval;
    private int sessionsUntilLongBreak;

    /**
     * Creates settings with specified intervals and session count.
     *
     * @param workInterval Work interval in seconds
     * @param shortBreakInterval Short break interval in seconds
     * @param longBreakInterval Long break interval in seconds
     * @param sessionsUntilLongBreak Number of sessions before long break
     */
    public Settings(int workInterval, int shortBreakInterval, int longBreakInterval, int sessionsUntilLongBreak) {
        this.workInterval = workInterval;
        this.shortBreakInterval = shortBreakInterval;
        this.longBreakInterval = longBreakInterval;
        this.sessionsUntilLongBreak = sessionsUntilLongBreak;
    }

    /**
     * Creates settings with default values.
     * Default: 25min work, 5min short break, 15min long break, 4 sessions until long break.
     */
    public Settings() {
        this(25 * 60, 5 * 60, 15 * 60, 4);
    }

    // Getters and setters
    /**
     * Gets the work interval in seconds.
     *
     * @return Work interval in seconds
     */
    public int getWorkInterval() { return workInterval; }

    /**
     * Sets the work interval in seconds.
     *
     * @param workInterval Work interval in seconds
     */
    public void setWorkInterval(int workInterval) { this.workInterval = workInterval; }
    
    /**
     * Gets the short break interval in seconds.
     *
     * @return Short break interval in seconds
     */
    public int getShortBreakInterval() { return shortBreakInterval; }

    /**
     * Sets the short break interval in seconds.
     *
     * @param shortBreakInterval Short break interval in seconds
     */
    public void setShortBreakInterval(int shortBreakInterval) { this.shortBreakInterval = shortBreakInterval; }
    
    /**
     * Gets the long break interval in seconds.
     *
     * @return Long break interval in seconds
     */
    public int getLongBreakInterval() { return longBreakInterval; }

    /**
     * Sets the long break interval in seconds.
     *
     * @param longBreakInterval Long break interval in seconds
     */
    public void setLongBreakInterval(int longBreakInterval) { this.longBreakInterval = longBreakInterval; }
    
    /**
     * Gets the number of sessions before a long break.
     *
     * @return Number of sessions before a long break
     */
    public int getSessionsUntilLongBreak() { return sessionsUntilLongBreak; }

    /**
     * Sets the number of sessions before a long break.
     *
     * @param sessionsUntilLongBreak Number of sessions before a long break
     */
    public void setSessionsUntilLongBreak(int sessionsUntilLongBreak) { this.sessionsUntilLongBreak = sessionsUntilLongBreak; }

    
}
