package com.pomodoro.ui;

import com.pomodoro.model.*;
import com.pomodoro.service.AnalyticsService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

public class AnalyticsPanel extends JPanel implements AnalyticsService.AnalyticsListener {
    private JTable statsTable;
    private DefaultTableModel tableModel;
    private JLabel totalPomodorosLabel;
    private JPanel chartsPanel;
    private JTabbedPane tabbedPane;
    private static final int DEFAULT_PERIODS = 30; // Збільшуємо кількість періодів за замовчуванням

    public AnalyticsPanel(AnalyticsService analyticsService) {
        analyticsService.addListener(this);
        initializeComponents();
        updateDisplay(analyticsService.getTaskStats(), analyticsService.getTotalPomodoros()); // Initialize table with saved data
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Initialize tabbed pane
        tabbedPane = new JTabbedPane();

        // Create table tab
        JPanel tableTab = createTableTab();
        tabbedPane.addTab("Statistics Table", tableTab);

        // Create charts tab
        JPanel chartsTab = createChartsTab();
        tabbedPane.addTab("Charts", chartsTab);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createTableTab() {
        JPanel tableTab = new JPanel(new BorderLayout(10, 10));
        
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

        tableTab.add(summaryPanel, BorderLayout.NORTH);
        tableTab.add(new JScrollPane(statsTable), BorderLayout.CENTER);

        return tableTab;
    }

    private JPanel createChartsTab() {
        JPanel chartsTab = new JPanel(new BorderLayout());
        JPanel chartsContainer = new JPanel(new GridLayout(3, 1, 10, 10));
        chartsContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        chartsPanel = chartsContainer;

        // Додаємо скролінг
        JScrollPane scrollPane = new JScrollPane(chartsContainer);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chartsTab.add(scrollPane, BorderLayout.CENTER);

        return chartsTab;
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

        // Update charts
        updateCharts(stats);
    }

    private void updateCharts(Map<String, TaskStats> stats) {
        chartsPanel.removeAll();

        // Daily chart
        JFreeChart dailyChart = createTimeChart(stats, "Daily Tasks", ChronoUnit.DAYS, DEFAULT_PERIODS);
        ChartPanel dailyChartPanel = createChartPanel(dailyChart, null);
        chartsPanel.add(dailyChartPanel);

        // Weekly chart
        JFreeChart weeklyChart = createTimeChart(stats, "Weekly Tasks", ChronoUnit.WEEKS, DEFAULT_PERIODS/7);
        ChartPanel weeklyChartPanel = createChartPanel(weeklyChart, null);
        chartsPanel.add(weeklyChartPanel);

        // Monthly chart
        JFreeChart monthlyChart = createTimeChart(stats, "Monthly Tasks", ChronoUnit.MONTHS, DEFAULT_PERIODS/30);
        ChartPanel monthlyChartPanel = createChartPanel(monthlyChart, null);
        chartsPanel.add(monthlyChartPanel);

        chartsPanel.revalidate();
        chartsPanel.repaint();
    }

    private ChartPanel createChartPanel(JFreeChart chart, Dimension preferredSize) {
        ChartPanel chartPanel = new ChartPanel(chart);
        
        // Встановлюємо розмір для ChartPanel замість chart
        chartPanel.setPreferredSize(new Dimension(
            Math.max(800, chartPanel.getPreferredSize().width),
            200
        ));
        
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setZoomInFactor(0.9);
        chartPanel.setZoomOutFactor(1.1);
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        chartPanel.setMouseZoomable(true);
        
        return chartPanel;
    }

    private JFreeChart createTimeChart(Map<String, TaskStats> stats, String title, ChronoUnit unit, int defaultPeriods) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        LocalDateTime now = LocalDateTime.now();

        // Find the earliest completion date
        LocalDateTime earliestDate = stats.values().stream()
            .map(TaskStats::getCompletionTime)
            .filter(date -> date != null)
            .min(LocalDateTime::compareTo)
            .orElse(now.minus(defaultPeriods, unit));

        // Calculate actual number of periods needed
        long actualPeriods = unit.between(earliestDate, now) + 1;
        
        // Group tasks by time period, using all available data
        Map<String, Long> completedTasks = stats.values().stream()
            .filter(stat -> stat.getCompletionTime() != null)
            .collect(Collectors.groupingBy(
                stat -> getPeriodLabel(stat.getCompletionTime(), unit),
                Collectors.counting()
            ));

        // Add data points for all periods from earliest to now
        for (long i = actualPeriods - 1; i >= 0; i--) {
            LocalDateTime periodStart = now.minus(i, unit);
            String label = formatPeriodLabel(periodStart, unit);
            dataset.addValue(completedTasks.getOrDefault(label, 0L), 
                           "Completed Tasks", label);
        }

        JFreeChart chart = ChartFactory.createBarChart(
            title,
            "Period",
            "Number of Tasks",
            dataset
        );

        // Налаштування відображення графіка
        chart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(
            org.jfree.chart.axis.CategoryLabelPositions.UP_45
        );

        return chart;
    }

    private boolean isWithinPeriod(LocalDateTime date, LocalDateTime now, ChronoUnit unit, int periods) {
        LocalDateTime startDate = now.minus(periods, unit);
        return !date.isBefore(startDate) && !date.isAfter(now);
    }

    private String getPeriodLabel(LocalDateTime date, ChronoUnit unit) {
        return switch (unit) {
            case DAYS -> date.format(DateTimeFormatter.ofPattern("MM/dd"));
            case WEEKS -> "Week " + date.get(WeekFields.ISO.weekOfWeekBasedYear());
            case MONTHS -> date.format(DateTimeFormatter.ofPattern("MMM yy"));
            default -> date.toString();
        };
    }

    private String formatPeriodLabel(LocalDateTime date, ChronoUnit unit) {
        return getPeriodLabel(date, unit);
    }
}
