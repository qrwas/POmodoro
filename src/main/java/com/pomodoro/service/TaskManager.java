package main.java.com.pomodoro.service;

import main.java.com.pomodoro.model.Task;
import java.util.*;

public class TaskManager {
    private List<Task> tasks = new ArrayList<>();
    private Task currentActiveTask;
    private List<TaskChangeListener> listeners = new ArrayList<>();

    public interface TaskChangeListener {
        void onTaskListChanged();
        void onTaskStatusChanged(Task task);
    }

    public void addListener(TaskChangeListener listener) {
        listeners.add(listener);
    }

    public Task getCurrentActiveTask() {
        return currentActiveTask;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public List<Task> getFilteredTasks(String filter) {
        return tasks.stream()
            .filter(task -> switch (filter) {
                case "All" -> true;
                case "Active" -> !task.isCompleted();
                case "Completed" -> task.isCompleted();
                default -> true;
            })
            .toList();
    }

    public void addTask(String name, int priority) {
        Task task = new Task(name, priority);
        task.setIndex(tasks.size());
        tasks.add(task);
        notifyTaskListChanged();
    }

    public void deleteTask(Task task) {
        if (task.isInProgress()) {
            throw new IllegalStateException("Cannot delete a task that is in progress");
        }
        if (task == currentActiveTask) {
            throw new IllegalStateException("Cannot delete the active task");
        }
        tasks.remove(task);
        notifyTaskListChanged();
    }

    public void updateTaskName(Task task, String newName) {
        task.setName(newName);
        notifyTaskStatusChanged(task);
    }

    public void startTask(Task task) {
        if (task.isCompleted()) {
            throw new IllegalStateException("Cannot start a completed task");
        }
        if (currentActiveTask != null && currentActiveTask != task) {
            throw new IllegalStateException("Another task is already in progress");
        }
        currentActiveTask = task;
        task.setInProgress(true);
        task.setCompleted(false);
        notifyTaskStatusChanged(task);
    }

    public void pauseTask(Task task) {
        task.setInProgress(false);
        notifyTaskStatusChanged(task);
    }

    public void completeTask(Task task) {
        task.setCompleted(true);
        task.setInProgress(false);
        if (task == currentActiveTask) {
            currentActiveTask = null;
        }
        notifyTaskStatusChanged(task);
    }

    public void resetTask(Task task) {
        task.setInProgress(false);
        task.setCompleted(false);
        if (task == currentActiveTask) {
            currentActiveTask = null;
        }
        notifyTaskStatusChanged(task);
    }

    public void sortTasks(int columnIndex) {
        tasks.sort((task1, task2) -> {
            // Always put completed tasks at the bottom
            if (task1.isCompleted() && !task2.isCompleted()) return 1;
            if (!task1.isCompleted() && task2.isCompleted()) return -1;
            
            // If both tasks have same completion status, sort by column
            return switch(columnIndex) {
                case 0 -> task1.getName().compareTo(task2.getName());
                case 1 -> Integer.compare(task1.getPriority(), task2.getPriority());
                default -> 0;
            };
        });
        notifyTaskListChanged();
    }

    public Task getTaskByIndex(int index) {
        if (index >= 0 && index < tasks.size()) {
            return tasks.get(index);
        }
        return null;
    }

    public Task findTaskByName(String name) {
        return tasks.stream()
            .filter(t -> t.getName().equals(name))
            .findFirst()
            .orElse(null);
    }

    public void updateTask(Task task, String newName, int newPriority) {
        task.setName(newName);
        task.setPriority(newPriority);
        notifyTaskStatusChanged(task);
    }

    public String getTaskStatus(Task task) {
        if (task.isCompleted()) return "Completed";
        if (task.isInProgress()) return "In Progress";
        return "Not Started";
    }

    public String getPriorityLabel(int priority) {
        return switch (priority) {
            case 1 -> "High";
            case 2 -> "Medium";
            case 3 -> "Low";
            default -> "Unknown";
        };
    }

    public int getPriorityValue(String priority) {
        return switch (priority) {
            case "High" -> 1;
            case "Medium" -> 2;
            case "Low" -> 3;
            default -> 4;
        };
    }

    private void notifyTaskListChanged() {
        listeners.forEach(TaskChangeListener::onTaskListChanged);
    }

    private void notifyTaskStatusChanged(Task task) {
        listeners.forEach(listener -> listener.onTaskStatusChanged(task));
    }
}
