package com.pomodoro.service;

import com.pomodoro.model.Task;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Manages tasks in the Pomodoro application.
 * Handles task creation, deletion, updates, and status changes.
 */
public class TaskManager {
    private final DataManager dataManager;
    private final AnalyticsService analyticsService;
    private List<Task> tasks = new ArrayList<>();
    private Task currentActiveTask;
    private List<TaskChangeListener> listeners = new ArrayList<>();

    /**
     * Creates a new TaskManager instance.
     *
     * @param dataManager Manager for persisting task data
     * @param analyticsService Service for tracking task analytics
     */
    public TaskManager(DataManager dataManager, AnalyticsService analyticsService) {
        this.dataManager = dataManager;
        this.analyticsService = analyticsService;
        loadSavedTasks();
    }

    /**
     * Interface for listening to task changes.
     */
    public interface TaskChangeListener {
        /** Called when the task list is modified */
        void onTaskListChanged();
        /** Called when a task's status changes */
        void onTaskStatusChanged(Task task);
    }

    /** Adds a listener for task changes */
    public void addListener(TaskChangeListener listener) {
        listeners.add(listener);
    }

    /** Gets the currently active task */
    public Task getCurrentActiveTask() {
        return currentActiveTask;
    }

    /** Returns a copy of all tasks */
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    /**
     * Returns filtered tasks based on their status.
     *
     * @param filter Filter criteria ("All", "Active", "Completed")
     * @return Filtered list of tasks
     */
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

    /**
     * Adds a new task to the list.
     *
     * @param name Task name
     * @param priority Task priority level
     */
    public void addTask(String name, int priority) {
        Task task = new Task(name, priority);
        task.setIndex(tasks.size());
        tasks.add(task);
        notifyTaskListChanged();
    }

    /**
     * Deletes a task if it's not in progress.
     *
     * @param task Task to delete
     * @throws IllegalStateException if task is in progress or active
     */
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

    /**
     * Updates the name of a task.
     *
     * @param task Task to update
     * @param newName New name for the task
     */
    public void updateTaskName(Task task, String newName) {
        task.setName(newName);
        notifyTaskStatusChanged(task);
    }

    /**
     * Starts a task with a specified duration.
     *
     * @param task Task to start
     * @param duration Duration in seconds
     * @throws IllegalStateException if task is completed or another task is in progress
     */
    public void startTask(Task task, int duration) {
        if (task.isCompleted()) {
            throw new IllegalStateException("Cannot start a completed task");
        }
        if (currentActiveTask != null && currentActiveTask != task) {
            throw new IllegalStateException("Another task is already in progress");
        }
        task.setPlannedDuration(duration);
        currentActiveTask = task;
        task.setInProgress(true);
        task.setCompleted(false);
        notifyTaskStatusChanged(task);
    }

    /**
     * Starts a task with a default duration of 25 minutes.
     *
     * @param task Task to start
     */
    public void startTask(Task task) {
        startTask(task, 25 * 60); // default 25 minutes
    }

    /**
     * Pauses a task.
     *
     * @param task Task to pause
     */
    public void pauseTask(Task task) {
        task.setInProgress(false);
        notifyTaskStatusChanged(task);
    }

    /**
     * Completes a task.
     *
     * @param task Task to complete
     */
    public void completeTask(Task task) {
        task.setCompleted(true);
        task.setInProgress(false);
        task.setCompletionTime(LocalDateTime.now()); // Set completion time
        if (task == currentActiveTask) {
            currentActiveTask = null;
        }
        notifyTaskStatusChanged(task);
        analyticsService.recordPomodoro(task, task.getPlannedDuration());
    }

    /**
     * Resets a task to its initial state.
     *
     * @param task Task to reset
     */
    public void resetTask(Task task) {
        task.setInProgress(false);
        task.setCompleted(false);
        if (task == currentActiveTask) {
            currentActiveTask = null;
        }
        notifyTaskStatusChanged(task);
    }

    /**
     * Sorts tasks based on a specified column index.
     *
     * @param columnIndex Index of the column to sort by
     */
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

    /**
     * Gets a task by its index.
     *
     * @param index Index of the task
     * @return Task at the specified index, or null if index is out of bounds
     */
    public Task getTaskByIndex(int index) {
        if (index >= 0 && index < tasks.size()) {
            return tasks.get(index);
        }
        return null;
    }

    /**
     * Finds a task by its name.
     *
     * @param name Name of the task
     * @return Task with the specified name, or null if not found
     */
    public Task findTaskByName(String name) {
        return tasks.stream()
            .filter(t -> t.getName().equals(name))
            .findFirst()
            .orElse(null);
    }

    /**
     * Updates a task's name and priority.
     *
     * @param task Task to update
     * @param newName New name for the task
     * @param newPriority New priority for the task
     */
    public void updateTask(Task task, String newName, int newPriority) {
        task.setName(newName);
        task.setPriority(newPriority);
        notifyTaskStatusChanged(task);
    }

    /**
     * Gets the status of a task.
     *
     * @param task Task to check
     * @return Status of the task ("Completed", "In Progress", "Not Started")
     */
    public String getTaskStatus(Task task) {
        if (task.isCompleted()) return "Completed";
        if (task.isInProgress()) return "In Progress";
        return "Not Started";
    }

    /**
     * Gets the label for a priority value.
     *
     * @param priority Priority value
     * @return Priority label ("High", "Medium", "Low", "Unknown")
     */
    public String getPriorityLabel(int priority) {
        return switch (priority) {
            case 1 -> "High";
            case 2 -> "Medium";
            case 3 -> "Low";
            default -> "Unknown";
        };
    }

    /**
     * Gets the priority value for a label.
     *
     * @param priority Priority label
     * @return Priority value (1 for "High", 2 for "Medium", 3 for "Low", 4 for "Unknown")
     */
    public int getPriorityValue(String priority) {
        return switch (priority) {
            case "High" -> 1;
            case "Medium" -> 2;
            case "Low" -> 3;
            default -> 4;
        };
    }

    private void loadSavedTasks() {
        tasks = dataManager.loadTasks();
        notifyTaskListChanged();
    }

    public void saveTasks() {
        dataManager.saveTasks(tasks);
    }

    private void notifyTaskListChanged() {
        listeners.forEach(TaskChangeListener::onTaskListChanged);
    }

    private void notifyTaskStatusChanged(Task task) {
        listeners.forEach(listener -> listener.onTaskStatusChanged(task));
    }
}
