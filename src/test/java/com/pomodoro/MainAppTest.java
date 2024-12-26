package com.pomodoro;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import java.io.File;
import javax.swing.*;
import com.pomodoro.model.Settings;
import com.pomodoro.di.ServiceContainer;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.awt.Component;
import java.awt.Container;

@RunWith(JUnit4.class)
public class MainAppTest {
    private ServiceContainer services;
    private File tempSettingsFile;
    private MainApp mainApp;

    @Before
    public void setUp() {
        services = new ServiceContainer();
        tempSettingsFile = new File("test_settings.json");
        mainApp = new MainApp();
    }

    @After
    public void tearDown() {
        if (tempSettingsFile.exists()) {
            tempSettingsFile.delete();
        }
        if (mainApp != null) {
            mainApp.dispose();
        }
    }

    @Test
    public void testSettingsSaveAndLoad() {
        // Arrange
        Settings originalSettings = services.getSettings();
        originalSettings.setWorkInterval(1500);        // 25 minutes
        originalSettings.setShortBreakInterval(300);   // 5 minutes
        originalSettings.setLongBreakInterval(900);    // 15 minutes
        originalSettings.setSessionsUntilLongBreak(4);

        // Act
        services.saveAll();
        ServiceContainer newServices = new ServiceContainer();
        Settings loadedSettings = newServices.getSettings();

        // Assert
        assertEquals(originalSettings.getWorkInterval(), loadedSettings.getWorkInterval());
        assertEquals(originalSettings.getShortBreakInterval(), loadedSettings.getShortBreakInterval());
        assertEquals(originalSettings.getLongBreakInterval(), loadedSettings.getLongBreakInterval());
        assertEquals(originalSettings.getSessionsUntilLongBreak(), loadedSettings.getSessionsUntilLongBreak());
    }

    @Test
    public void testMainAppInitialization() {
        assertNotNull("MainApp should not be null", mainApp);
        assertTrue("MainApp should be visible", mainApp.isVisible());
        assertEquals("Window title should match", "Pomodoro Task Tracker", mainApp.getTitle());
    }

    @Test
    public void testTabbedPaneExists() {
        JTabbedPane tabbedPane = (JTabbedPane) findComponent(mainApp, JTabbedPane.class);
        assertNotNull("TabbedPane should exist", tabbedPane);
        assertEquals("Should have 2 tabs", 2, tabbedPane.getTabCount());
        assertEquals("First tab should be Main", "Main", tabbedPane.getTitleAt(0));
        assertEquals("Second tab should be Analytics", "Analytics", tabbedPane.getTitleAt(1));
    }

    private Component findComponent(Container container, Class<?> componentClass) {
        for (Component component : container.getComponents()) {
            if (componentClass.isInstance(component)) {
                return component;
            }
            if (component instanceof Container) {
                Component found = findComponent((Container) component, componentClass);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
