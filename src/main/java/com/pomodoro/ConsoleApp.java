package com.pomodoro;

import com.pomodoro.di.ServiceContainer;
import com.pomodoro.model.Task;
import com.pomodoro.model.Settings;
import com.pomodoro.service.TaskManager;

import java.util.List;
import java.util.Scanner;

/**
 * Console application for managing Pomodoro tasks.
 * Provides a text-based interface for interacting with the Pomodoro Task Tracker.
 */
public class ConsoleApp {
    private final ServiceContainer services;
    private final TaskManager taskManager;
    private final Scanner scanner;

    public ConsoleApp() {
        this.services = new ServiceContainer();
        this.taskManager = services.getTaskManager();
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        while (true) {
            printMainMenu();
            int choice = getUserChoice();
            handleUserChoice(choice);
        }
    }

    private void printMainMenu() {
        System.out.println("Pomodoro Task Tracker");
        System.out.println("1. View Tasks");
        System.out.println("2. Add Task");
        System.out.println("3. Start Task");
        System.out.println("4. Complete Task");
        System.out.println("5. Settings");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    private int getUserChoice() {
        return Integer.parseInt(scanner.nextLine());
    }

    private void handleUserChoice(int choice) {
        switch (choice) {
            case 1 -> viewTasks();
            case 2 -> addTask();
            case 3 -> startTask();
            case 4 -> completeTask();
            case 5 -> showSettings();
            case 6 -> exitApplication();
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }

    private void viewTasks() {
        List<Task> tasks = taskManager.getAllTasks();
        if (tasks.isEmpty()) {
            System.out.println("No tasks available.");
        } else {
            System.out.println("Tasks:");
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                System.out.printf("%d. %s (Priority: %d, Status: %s)%n", i + 1, task.getName(), task.getPriority(), taskManager.getTaskStatus(task));
            }
        }
    }

    private void addTask() {
        System.out.print("Enter task name: ");
        String name = scanner.nextLine();
        System.out.print("Enter task priority (1-High, 2-Medium, 3-Low): ");
        int priority = Integer.parseInt(scanner.nextLine());
        taskManager.addTask(name, priority);
        System.out.println("Task added successfully.");
    }

    private void startTask() {
        List<Task> tasks = taskManager.getFilteredTasks("Active");
        if (tasks.isEmpty()) {
            System.out.println("No active tasks available to start.");
            return;
        }

        System.out.println("Active Tasks:");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            System.out.printf("%d. %s (Priority: %d)%n", i + 1, task.getName(), task.getPriority());
        }

        System.out.print("Enter task number to start: ");
        int taskNumber = Integer.parseInt(scanner.nextLine()) - 1;
        if (taskNumber < 0 || taskNumber >= tasks.size()) {
            System.out.println("Invalid task number.");
            return;
        }

        Task task = tasks.get(taskNumber);
        try {
            int workInterval = services.getSettings().getWorkInterval();
            taskManager.startTask(task, workInterval);
            System.out.println("Task started successfully. It will be completed automatically when time is up.");
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void completeTask() {
        System.out.print("Enter task number to complete: ");
        int taskNumber = Integer.parseInt(scanner.nextLine()) - 1;
        Task task = taskManager.getTaskByIndex(taskNumber);
        if (task != null) {
            taskManager.completeTask(task);
            System.out.println("Task completed successfully.");
        } else {
            System.out.println("Invalid task number.");
        }
    }

    private void showSettings() {
        Settings settings = services.getSettings();
        System.out.printf("Current Settings:%nWork Interval: %d minutes%nShort Break Interval: %d minutes%nLong Break Interval: %d minutes%nSessions Until Long Break: %d%n",
                settings.getWorkInterval() / 60,
                settings.getShortBreakInterval() / 60,
                settings.getLongBreakInterval() / 60,
                settings.getSessionsUntilLongBreak());

        System.out.print("Do you want to update settings? (yes/no): ");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("yes")) {
            updateSettings(settings);
        }
    }

    private void updateSettings(Settings settings) {
        System.out.print("Enter new work interval (minutes): ");
        settings.setWorkInterval(Integer.parseInt(scanner.nextLine()) * 60);
        System.out.print("Enter new short break interval (minutes): ");
        settings.setShortBreakInterval(Integer.parseInt(scanner.nextLine()) * 60);
        System.out.print("Enter new long break interval (minutes): ");
        settings.setLongBreakInterval(Integer.parseInt(scanner.nextLine()) * 60);
        System.out.print("Enter new sessions until long break: ");
        settings.setSessionsUntilLongBreak(Integer.parseInt(scanner.nextLine()));
        services.getDataManager().saveSettings(settings);
        System.out.println("Settings updated successfully.");
    }

    private void exitApplication() {
        services.saveAll();
        System.out.println("Data saved. Exiting application.");
        System.exit(0);
    }

    public static void main(String[] args) {
        new ConsoleApp().run();
    }
}
