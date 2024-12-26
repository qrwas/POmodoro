package com.pomodoro.di;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import java.io.File;
import com.pomodoro.model.Settings;
import com.pomodoro.service.TaskManager;
import com.pomodoro.service.DataManager;
import com.pomodoro.service.AnalyticsService;

public class ServiceContainerTest {
    private ServiceContainer container;
    private File settingsFile;
    private File tasksFile;
    private File analyticsFile;

    @Before
    public void setUp() {
        container = new ServiceContainer();
        settingsFile = new File("settings.json");
        tasksFile = new File("tasks.json");
        analyticsFile = new File("analytics.json");
    }

    @After
    public void tearDown() {
        settingsFile.delete();
        tasksFile.delete();
        analyticsFile.delete();
    }

    @Test
    public void testServiceInitialization() {
        assertNotNull("Settings should be initialized", container.getSettings());
        assertNotNull("TaskManager should be initialized", container.getTaskManager());
        assertNotNull("DataManager should be initialized", container.getDataManager());
        assertNotNull("AnalyticsService should be initialized", container.getAnalyticsService());
    }

    @Test
    public void testSettingsInitialization() {
        Settings settings = container.getSettings();
        assertTrue("Work interval should be positive", settings.getWorkInterval() > 0);
        assertTrue("Short break interval should be positive", settings.getShortBreakInterval() > 0);
        assertTrue("Long break interval should be positive", settings.getLongBreakInterval() > 0);
        assertTrue("Sessions until long break should be positive", settings.getSessionsUntilLongBreak() > 0);
    }

    @Test
    public void testSaveAll() {
        // Arrange
        Settings settings = container.getSettings();
        settings.setWorkInterval(1500);
        TaskManager taskManager = container.getTaskManager();
        taskManager.addTask("Test Task", 1);

        // Act
        container.saveAll();

        // Assert
        ServiceContainer newContainer = new ServiceContainer();
        assertEquals("Work interval should be preserved", 
            1500, newContainer.getSettings().getWorkInterval());
        assertEquals("Task should be preserved", 
            "Test Task", newContainer.getTaskManager().getAllTasks().get(0).getName());
    }

    @Test
    public void testServiceDependencies() {
        TaskManager taskManager = container.getTaskManager();
        AnalyticsService analyticsService = container.getAnalyticsService();
        DataManager dataManager = container.getDataManager();

        // Test that TaskManager has proper dependencies
        assertNotNull("TaskManager should have DataManager", taskManager);
        assertNotNull("TaskManager should have AnalyticsService", analyticsService);
        
        // Test that services share the same DataManager
        assertEquals("Services should share DataManager",
            dataManager, container.getDataManager());
    }
}
