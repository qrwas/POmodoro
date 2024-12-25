package main.java.com.pomodoro;

import javax.swing.*;
import java.awt.*;
import main.java.com.pomodoro.ui.MainPanel;
import main.java.com.pomodoro.ui.SettingsDialog;

public class MainApp extends JFrame {
    private JTabbedPane tabbedPane;
    private MainPanel mainPanel;

    public MainApp() {
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
        
        // Main tab with new MainPanel
        
        // Analytics tab
        JPanel analyticsPanel = new JPanel();
        analyticsPanel.setLayout(new BorderLayout());

        tabbedPane.addTab("Main", mainPanel);
        tabbedPane.addTab("Analytics", analyticsPanel);

        add(tabbedPane);
    }

    private void showSettings() {
        SettingsDialog.Settings currentSettings = mainPanel.getCurrentSettings();
        SettingsDialog dialog = new SettingsDialog(
            this,
            currentSettings,
            mainPanel::applySettings
        );
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp());
    }
}
