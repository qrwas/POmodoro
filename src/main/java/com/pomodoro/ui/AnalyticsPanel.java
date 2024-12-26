package com.pomodoro.ui;

import com.pomodoro.model.*;
import com.pomodoro.service.AnalyticsService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

public class AnalyticsPanel extends JPanel implements AnalyticsService.AnalyticsListener {
    private JTable statsTable;
    private DefaultTableModel tableModel;
    private JLabel totalPomodorosLabel;

    public AnalyticsPanel(AnalyticsService analyticsService) {
        analyticsService.addListener(this);
        initializeComponents();
        updateDisplay(analyticsService.getTaskStats(), analyticsService.getTotalPomodoros()); // Initialize table with saved data
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Summary Panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalPomodorosLabel = new JLabel("Total Pomodoro Sessions: 0");
        totalPomodorosLabel.setFont(new Font("Arial", Font.BOLD, 16));
        summaryPanel.add(totalPomodorosLabel);

        // Table Panel
        tableModel = new DefaultTableModel(
            new String[]{"Task", "Total Time"}, 
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
            .forEach(stat -> tableModel.addRow(new Object[]{
                stat.getTaskName(),
                stat.getFormattedTimeSpent()
            }));
    }
}
