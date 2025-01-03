package com.pomodoro.service;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.pomodoro.model.Settings;
import com.pomodoro.model.Task;
import java.util.List;

/**
 * Unit tests for TaskManager class.
 * Tests task management operations including adding, starting, completing, and filtering tasks.
 */
public class TaskManagerTest {
    private TestDataManager dataManager;
    private AnalyticsService analyticsService;
    private TaskManager taskManager;
    private Settings settings;

    /**
     * Sets up test environment before each test.
     * Initializes test data manager and services.
     */
    @Before
    public void setUp() {
        dataManager = new TestDataManager();
        analyticsService = new AnalyticsService(dataManager);
        settings = new Settings();
        taskManager = new TaskManager(dataManager, analyticsService, settings);
    }

    /**
     * Tests adding a new task.
     * Verifies task properties are correctly set.
     */
    @Test
    public void testAddTask() {
        // Arrange & Act
        taskManager.addTask("Test Task", 1);
        List<Task> tasks = taskManager.getAllTasks();

        // Assert
        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getName());
        assertEquals(1, tasks.get(0).getPriority());
    }

    /**
     * Tests starting a task.
     * Verifies task status changes and current active task is set.
     */
    @Test
    public void testStartTask() {
        // Arrange
        taskManager.addTask("Test Task", 1);
        Task task = taskManager.getAllTasks().get(0);

        // Act
        taskManager.startTask(task);

        // Assert
        assertTrue(task.isInProgress());
        assertEquals(task, taskManager.getCurrentActiveTask());
    }

    @Test(expected = IllegalStateException.class)
    public void testStartTaskWhenAnotherIsActive() {
        // Arrange
        taskManager.addTask("Task 1", 1);
        taskManager.addTask("Task 2", 1);
        List<Task> tasks = taskManager.getAllTasks();

        // Act
        taskManager.startTask(tasks.get(0));
        taskManager.startTask(tasks.get(1)); // Should throw exception
    }

    @Test
    public void testCompleteTask() {
        // Arrange
        taskManager.addTask("Test Task", 1);
        Task task = taskManager.getAllTasks().get(0);
        taskManager.startTask(task);

        // Act
        taskManager.completeTask(task);

        // Assert
        assertTrue(task.isCompleted());
        assertFalse(task.isInProgress());
        assertNull(taskManager.getCurrentActiveTask());
    }

    @Test
    public void testSortTasks() {
        // Arrange
        taskManager.addTask("B Task", 2);
        taskManager.addTask("A Task", 1);
        taskManager.addTask("C Task", 3);

        // Act
        taskManager.sortTasks(0); // Sort by name
        List<Task> tasks = taskManager.getAllTasks();

        // Assert
        assertEquals("A Task", tasks.get(0).getName());
        assertEquals("B Task", tasks.get(1).getName());
        assertEquals("C Task", tasks.get(2).getName());
    }

    @Test
    public void testFilterTasks() {
        // Arrange
        taskManager.addTask("Task 1", 1);
        taskManager.addTask("Task 2", 1);
        Task task = taskManager.getAllTasks().get(0);
        taskManager.completeTask(task);

        // Act & Assert
        assertEquals(1, taskManager.getFilteredTasks("Completed").size());
        assertEquals(1, taskManager.getFilteredTasks("Active").size());
        assertEquals(2, taskManager.getFilteredTasks("All").size());
    }
}
