package main.java.com.pomodoro.ui;

import main.java.com.pomodoro.model.*;
import main.java.com.pomodoro.service.AnalyticsService;
import main.java.com.pomodoro.service.TaskManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

public class AnalyticsPanel extends JPanel implements AnalyticsService.AnalyticsListener {
    private final TaskManager taskManager;
    private JTable statsTable;
    private DefaultTableModel tableModel;
    private JLabel totalPomodorosLabel;

    public AnalyticsPanel(TaskManager taskManager, AnalyticsService analyticsService) {
        this.taskManager = taskManager;
        analyticsService.addListener(this);
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Summary Panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalPomodorosLabel = new JLabel("Total Pomodoro Sessions: 0");
        totalPomodorosLabel.setFont(new Font("Arial", Font.BOLD, 16));
        summaryPanel.add(totalPomodorosLabel);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(
            new String[]{"Task", "Completed Pomodoros", "Total Time"}, 
            0
        );
        statsTable = new JTable(tableModel);
        statsTable.setFillsViewportHeight(true);

        // Add panels
        add(summaryPanel, BorderLayout.NORTH);
        add(new JScrollPane(statsTable), BorderLayout.CENTER);
    }

    @Override
    public void onStatsUpdated(Map<String, TaskStats> stats, int totalPomodoros) {
        updateDisplay(stats, totalPomodoros);
    }

    private void updateDisplay(Map<String, TaskStats> stats, int totalPomodoros) {
        totalPomodorosLabel.setText("Total Pomodoro Sessions: " + totalPomodoros);

        tableModel.setRowCount(0);
        stats.values().stream()
            .sorted((s1, s2) -> Integer.compare(s2.getCompletedPomodoros(), s1.getCompletedPomodoros()))
            .forEach(stat -> tableModel.addRow(new Object[]{
                stat.getTaskName(),
                stat.getCompletedPomodoros(),
                stat.getFormattedTimeSpent()
            }));
    }
}
