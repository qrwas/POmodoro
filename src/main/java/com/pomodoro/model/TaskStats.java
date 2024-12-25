package main.java.com.pomodoro.model;

public class TaskStats {
    private final String taskName;
    private int completedPomodoros;
    private long totalTimeSpent; // in seconds

    public TaskStats(String taskName) {
        this.taskName = taskName;
        this.completedPomodoros = 0;
        this.totalTimeSpent = 0;
    }

    public void addPomodoro(int duration) {
        completedPomodoros++;
        totalTimeSpent += duration;
    }

    public String getTaskName() { return taskName; }
    public int getCompletedPomodoros() { return completedPomodoros; }
    public long getTotalTimeSpent() { return totalTimeSpent; }
    
    public String getFormattedTimeSpent() {
        long hours = totalTimeSpent / 3600;
        long minutes = (totalTimeSpent % 3600) / 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    // Add setters for deserialization
    public void setCompletedPomodoros(int completedPomodoros) {
        this.completedPomodoros = completedPomodoros;
    }

    public void setTotalTimeSpent(long totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }
}
