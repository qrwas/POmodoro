package main.java.com.pomodoro.model;

public class Settings {
    private int workInterval;
    private int shortBreakInterval;
    private int longBreakInterval;
    private int sessionsUntilLongBreak;

    public Settings(int workInterval, int shortBreakInterval, int longBreakInterval, int sessionsUntilLongBreak) {
        this.workInterval = workInterval;
        this.shortBreakInterval = shortBreakInterval;
        this.longBreakInterval = longBreakInterval;
        this.sessionsUntilLongBreak = sessionsUntilLongBreak;
    }

    // Default settings constructor
    public Settings() {
        this(25 * 60, 5 * 60, 15 * 60, 4);
    }

    // Getters and setters
    public int getWorkInterval() { return workInterval; }
    public void setWorkInterval(int workInterval) { this.workInterval = workInterval; }
    
    public int getShortBreakInterval() { return shortBreakInterval; }
    public void setShortBreakInterval(int shortBreakInterval) { this.shortBreakInterval = shortBreakInterval; }
    
    public int getLongBreakInterval() { return longBreakInterval; }
    public void setLongBreakInterval(int longBreakInterval) { this.longBreakInterval = longBreakInterval; }
    
    public int getSessionsUntilLongBreak() { return sessionsUntilLongBreak; }
    public void setSessionsUntilLongBreak(int sessionsUntilLongBreak) { this.sessionsUntilLongBreak = sessionsUntilLongBreak; }
}
