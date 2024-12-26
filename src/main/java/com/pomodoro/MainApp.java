package com.pomodoro;

import javax.swing.*;
import java.awt.*;
import com.pomodoro.ui.*;
import com.pomodoro.model.Settings;
import com.pomodoro.service.AnalyticsService;
import com.pomodoro.di.ServiceContainer;

public class MainApp extends JFrame {
    private final ServiceContainer services;
    private JTabbedPane tabbedPane;
    private MainPanel mainPanel;
    private AnalyticsPanel analyticsPanel;

    public MainApp() {
        this.services = new ServiceContainer();
        initializeUI();
        setupWindowListener();
    }

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

    private void setupWindowListener() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                services.saveAll();
                System.exit(0);
            }
        });
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem settingsItem = new JMenuItem("Settings");
        settingsItem.addActionListener(e -> showSettings());
        
        fileMenu.add(settingsItem);
        fileMenu.addSeparator();
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(new JMenuItem("About"));

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

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
            }
        );
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp());
    }
}
