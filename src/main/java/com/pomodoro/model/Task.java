package main.java.com.pomodoro.model;

public class Task {
    private String name;
    private int priority;
    private boolean completed;

    public Task(String name, int priority) {
        this.name = name;
        this.priority = priority;
        this.completed = false;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
