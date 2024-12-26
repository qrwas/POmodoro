package com.pomodoro;

import javax.swing.*;
import com.pomodoro.ui.*;
import com.pomodoro.model.Settings;
import com.pomodoro.di.ServiceContainer;

/**
 * Main application class that initializes and manages the Pomodoro Task Tracker application.
 * This class sets up the main window, UI components, and handles the application lifecycle.
 */
public class MainApp extends JFrame {
    private final ServiceContainer services;
    private JTabbedPane tabbedPane;
    private MainPanel mainPanel;
    private AnalyticsPanel analyticsPanel;

    /**
     * Constructs a new MainApp instance.
     * Initializes the service container and sets up the user interface.
     */
    public MainApp() {
        this.services = new ServiceContainer();
        initializeUI();
        setupWindowListener();
    }

    /**
     * Initializes the main application window and its UI components.
     * Sets up the window properties, creates the menu bar, and initializes the tabbed pane.
     */
    private void initializeUI() {
        setTitle("Pomodoro Task Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Create menu bar
        createMenuBar();

        // Create tabbed pane
        createTabbedPane();

        setVisible(true);
    }

    /**
     * Sets up the window listener to handle application shutdown.
     * Saves all data when the application is closed.
     */
    private void setupWindowListener() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                services.saveAll();
                System.exit(0);
            }
        });
    }

    /**
     * Creates and configures the application's menu bar.
     * Includes File menu with Settings and Exit options, and Help menu with About option.
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem settingsItem = new JMenuItem("Settings");
        settingsItem.addActionListener(e -> showSettings());
        
        fileMenu.add(settingsItem);
        fileMenu.addSeparator();
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> {
            services.saveAll();
            System.exit(0);
        });
        fileMenu.add(exitItem);
        
        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(new JMenuItem("About"));

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    /**
     * Creates and configures the main tabbed pane containing the Main and Analytics panels.
     * Initializes the panels with their respective services and updates interval settings.
     */
    private void createTabbedPane() {
        tabbedPane = new JTabbedPane();
        mainPanel = new MainPanel(services.getTaskManager());
        analyticsPanel = new AnalyticsPanel(
            services.getAnalyticsService()
        );
        
        Settings settings = services.getSettings();
        mainPanel.updateIntervals(
            settings.getWorkInterval() / 60,
            settings.getShortBreakInterval() / 60,
            settings.getLongBreakInterval() / 60,
            settings.getSessionsUntilLongBreak()
        );

        tabbedPane.addTab("Main", mainPanel);
        tabbedPane.addTab("Analytics", analyticsPanel);
        add(tabbedPane);
    }

    /**
     * Displays the settings dialog and handles settings updates.
     * Updates the UI components and saves the new settings when changes are applied.
     */
    private void showSettings() {
        Settings currentSettings = services.getSettings();
        SettingsDialog dialog = new SettingsDialog(
            this,
            currentSettings,
            settings -> {
                services.getDataManager().saveSettings(settings);
                mainPanel.updateIntervals(
                    settings.getWorkInterval() / 60,
                    settings.getShortBreakInterval() / 60,
                    settings.getLongBreakInterval() / 60,
                    settings.getSessionsUntilLongBreak()
                );
                currentSettings.setWorkInterval(settings.getWorkInterval());
                currentSettings.setLongBreakInterval(settings.getLongBreakInterval());
                currentSettings.setShortBreakInterval(settings.getShortBreakInterval());
                currentSettings.setSessionsUntilLongBreak(settings.getSessionsUntilLongBreak());
                System.out.println("Settings saved: " + services.getSettings().getWorkInterval());
            }
        );
        dialog.setVisible(true);
    }

    /**
     * The main entry point of the application.
     * Creates and shows the main application window on the Event Dispatch Thread.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp());
    }
}
