package main.java.com.pomodoro.model;

public class TaskStats {
    private final String taskName;
    private long totalTimeSpent; // in seconds

    public TaskStats(String taskName) {
        this.taskName = taskName;
        this.totalTimeSpent = 0;
    }

    public void addPomodoro(int duration) {
        totalTimeSpent += duration;
    }

    public String getTaskName() { return taskName; }
    public long getTotalTimeSpent() { return totalTimeSpent; }
    
    public String getFormattedTimeSpent() {
        long hours = totalTimeSpent / 3600;
        long minutes = (totalTimeSpent % 3600) / 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    public void setTotalTimeSpent(long totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }
}
