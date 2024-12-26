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
    private JTabbedPane tabbedPane;
    private static final int DEFAULT_PERIODS = 30; // Збільшуємо кількість періодів за замовчуванням

    public AnalyticsPanel(AnalyticsService analyticsService) {
        analyticsService.addListener(this);
        initializeComponents();
        updateDisplay(analyticsService.getTaskStats(), analyticsService.getTotalPomodoros()); // Initialize table with saved data
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        
        tabbedPane = new JTabbedPane();

        // Create table tab
        tabbedPane.addTab("Statistics Table", createTableTab());
        
        // Create separate tabs for each chart
        tabbedPane.addTab("Daily Stats", createSingleChartTab("Daily Tasks", ChronoUnit.DAYS, DEFAULT_PERIODS));
        tabbedPane.addTab("Weekly Stats", createSingleChartTab("Weekly Tasks", ChronoUnit.WEEKS, DEFAULT_PERIODS/7));
        tabbedPane.addTab("Monthly Stats", createSingleChartTab("Monthly Tasks", ChronoUnit.MONTHS, DEFAULT_PERIODS/30));

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

    private JPanel createSingleChartTab(String title, ChronoUnit unit, int periods) {
        JPanel chartTab = new JPanel(new BorderLayout());
        chartTab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create main panel with chart
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel chartHolder = new JPanel(new BorderLayout());
        chartHolder.setName(title);
        mainPanel.add(chartHolder, BorderLayout.CENTER);
        
        // Add navigation panel at the bottom
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prevButton = new JButton("←");
        JButton nextButton = new JButton("→");
        JLabel pageLabel = new JLabel("Page 1");
        
        navPanel.add(prevButton);
        navPanel.add(pageLabel);
        navPanel.add(nextButton);
        
        // Store navigation components in the chartHolder for later access
        chartHolder.putClientProperty("prevButton", prevButton);
        chartHolder.putClientProperty("nextButton", nextButton);
        chartHolder.putClientProperty("pageLabel", pageLabel);
        
        mainPanel.add(navPanel, BorderLayout.SOUTH);
        chartTab.add(mainPanel, BorderLayout.CENTER);
        
        return chartTab;
    }

    @Override
    public void onStatsUpdated(Map<String, TaskStats> stats, int totalPomodoros) {
        updateDisplay(stats, totalPomodoros);
    }

    private void updateDisplay(Map<String, TaskStats> stats, int totalPomodoros) {
        totalPomodorosLabel.setText("Total Pomodoro Sessions: " + totalPomodoros);

        // Update table
        tableModel.setRowCount(0);
        stats.values().stream()
            .forEach(stat -> tableModel.addRow(new Object[]{
                stat.getTaskName(),
                stat.getFormattedTimeSpent()
            }));

        // Update each chart in its tab
        updateChart(stats, "Daily Tasks", ChronoUnit.DAYS, DEFAULT_PERIODS);
        updateChart(stats, "Weekly Tasks", ChronoUnit.WEEKS, DEFAULT_PERIODS/7);
        updateChart(stats, "Monthly Tasks", ChronoUnit.MONTHS, DEFAULT_PERIODS/30);
    }

    private void updateChart(Map<String, TaskStats> stats, String title, ChronoUnit unit, int periods) {
        // Find the tab with this chart
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            Component comp = tabbedPane.getComponentAt(i);
            if (comp instanceof JPanel panel) {
                // Шукаємо компоненти рекурсивно
                JPanel chartHolder = findChartHolder(panel, title);
                if (chartHolder != null) {
                    // Create and add the new chart
                    chartHolder.removeAll();
                    JFreeChart chart = createTimeChart(stats, title, unit, periods);
                    ChartPanel chartPanel = createChartPanel(chart, null);
                    chartHolder.add(chartPanel, BorderLayout.CENTER);
                    chartHolder.revalidate();
                    chartHolder.repaint();
                    break;
                }
            }
        }
    }

    private JPanel findChartHolder(Container container, String title) {
        // Перевіряємо чи поточний контейнер є шуканою панеллю
        if (container instanceof JPanel 
            && title.equals(container.getName())) {
            return (JPanel) container;
        }

        // Рекурсивно шукаємо у всіх компонентах
        for (Component comp : container.getComponents()) {
            if (comp instanceof Container) {
                JPanel result = findChartHolder((Container) comp, title);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private ChartPanel createChartPanel(JFreeChart chart, Dimension preferredSize) {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        
        // Disable zooming since we're using scrolling
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);
        
        return chartPanel;
    }

    private JFreeChart createTimeChart(Map<String, TaskStats> stats, String title, ChronoUnit unit, int defaultPeriods) {
        DefaultCategoryDataset baseDataset = new DefaultCategoryDataset();
        LocalDateTime now = LocalDateTime.now();

        // Find the earliest completion date
        LocalDateTime earliestDate = stats.values().stream()
            .map(TaskStats::getCompletionTime)
            .filter(date -> date != null)
            .min(LocalDateTime::compareTo)
            .orElse(now.minus(defaultPeriods, unit));

        // Calculate actual number of periods needed
        long actualPeriods = unit.between(earliestDate, now) + 1;
        
        // Group tasks by time period
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
            baseDataset.addValue(completedTasks.getOrDefault(label, 0L), 
                               "Completed Tasks", label);
        }

        // Create sliding dataset with 30-day window
        org.jfree.data.category.SlidingCategoryDataset slidingDataset = 
            new org.jfree.data.category.SlidingCategoryDataset(baseDataset, 0, 30);

        JFreeChart chart = ChartFactory.createBarChart(
            title,
            "Period",
            "Number of Tasks",
            slidingDataset
        );

        // Configure chart appearance
        org.jfree.chart.plot.CategoryPlot plot = chart.getCategoryPlot();
        plot.getDomainAxis().setCategoryLabelPositions(
            org.jfree.chart.axis.CategoryLabelPositions.UP_45
        );
        
        // Store the sliding dataset in the chart's properties for later access
        chart.setNotify(false); // Prevent immediate redraws
        chart.addPropertyChangeListener("sliding.dataset", 
            evt -> plot.setDataset((org.jfree.data.category.SlidingCategoryDataset)evt.getNewValue()));
        chart.setProperty("sliding.dataset", slidingDataset);
        chart.setNotify(true);

        // Setup navigation buttons
        JPanel chartHolder = findChartHolder(this, title);
        if (chartHolder != null) {
            JButton prevButton = (JButton)chartHolder.getClientProperty("prevButton");
            JButton nextButton = (JButton)chartHolder.getClientProperty("nextButton");
            JLabel pageLabel = (JLabel)chartHolder.getClientProperty("pageLabel");
            
            int maxPage = (int)Math.ceil(baseDataset.getColumnCount() / 30.0);
            
            prevButton.addActionListener(e -> {
                int firstIdx = slidingDataset.getFirstCategoryIndex();
                if (firstIdx > 0) {
                    slidingDataset.setFirstCategoryIndex(Math.max(0, firstIdx - 30));
                    updatePageLabel(pageLabel, slidingDataset, baseDataset);
                }
            });
            
            nextButton.addActionListener(e -> {
                int firstIdx = slidingDataset.getFirstCategoryIndex();
                int maxIdx = baseDataset.getColumnCount() - 30;
                if (firstIdx < maxIdx) {
                    slidingDataset.setFirstCategoryIndex(Math.min(maxIdx, firstIdx + 30));
                    updatePageLabel(pageLabel, slidingDataset, baseDataset);
                }
            });
            
            // Initialize page label
            updatePageLabel(pageLabel, slidingDataset, baseDataset);
        }

        return chart;
    }

    private void updatePageLabel(JLabel label, org.jfree.data.category.SlidingCategoryDataset sliding, 
                               DefaultCategoryDataset base) {
        int currentPage = (sliding.getFirstCategoryIndex() / 30) + 1;
        int totalPages = (int)Math.ceil(base.getColumnCount() / 30.0);
        label.setText(String.format("Page %d of %d", currentPage, totalPages));
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
