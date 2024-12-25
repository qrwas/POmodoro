package com.pomodoro;

import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {
    private JTabbedPane tabbedPane;

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
        fileMenu.add(new JMenuItem("Settings"));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem("Exit"));
        
        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(new JMenuItem("About"));

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private void createTabbedPane() {
        tabbedPane = new JTabbedPane();
        
        // Main tab
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // Analytics tab
        JPanel analyticsPanel = new JPanel();
        analyticsPanel.setLayout(new BorderLayout());

        tabbedPane.addTab("Main", mainPanel);
        tabbedPane.addTab("Analytics", analyticsPanel);

        add(tabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp());
    }
}
