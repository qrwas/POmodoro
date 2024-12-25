package main.java.com.pomodoro;

import javax.swing.*;
import java.awt.*;
import main.java.com.pomodoro.ui.MainPanel;
import main.java.com.pomodoro.model.Settings;
import main.java.com.pomodoro.ui.SettingsDialog;

public class MainApp extends JFrame {
    private JTabbedPane tabbedPane;
    private MainPanel mainPanel;
    private Settings appSettings;

    public MainApp() {
        appSettings = new Settings(); // Initialize with default settings
        initializeUI();
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
        mainPanel = new MainPanel();
        mainPanel.updateIntervals(
            appSettings.getWorkInterval() / 60,
            appSettings.getShortBreakInterval() / 60,
            appSettings.getLongBreakInterval() / 60,
            appSettings.getSessionsUntilLongBreak()
        );
        
        // Analytics tab
        JPanel analyticsPanel = new JPanel();
        analyticsPanel.setLayout(new BorderLayout());

        tabbedPane.addTab("Main", mainPanel);
        tabbedPane.addTab("Analytics", analyticsPanel);

        add(tabbedPane);
    }

    private void showSettings() {
        SettingsDialog dialog = new SettingsDialog(
            this,
            appSettings,
            settings -> {
                appSettings = settings;
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
