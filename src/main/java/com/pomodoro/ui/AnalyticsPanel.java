package com.pomodoro.ui;

import com.pomodoro.model.*;
import com.pomodoro.service.AnalyticsService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.SlidingCategoryDataset;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Panel that displays analytics and statistics about Pomodoro sessions.
 * Shows charts, tables, and productivity trends.
 */
public class AnalyticsPanel extends JPanel implements AnalyticsService.AnalyticsListener {
    private JTable statsTable;
    private DefaultTableModel tableModel;
    private JLabel totalPomodorosLabel;
    private JLabel productiveHoursLabel;
    private JLabel productiveDaysLabel;
    private JTabbedPane tabbedPane;
    private static final int DEFAULT_PERIODS = 30; // Збільшуємо кількість періодів за замовчуванням

    /**
     * Creates a new analytics panel.
     *
     * @param analyticsService Service providing analytics data
     */
    public AnalyticsPanel(AnalyticsService analyticsService) {
        analyticsService.addListener(this);
        initializeComponents();
        updateDisplay(analyticsService.getTaskStats(), analyticsService.getTotalPomodoros()); // Initialize table with saved data
    }

    /**
     * Initializes all UI components of the analytics panel.
     */
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

    /**
     * Creates the statistics table tab.
     *
     * @return Panel containing the statistics table
     */
    private JPanel createTableTab() {
        JPanel tableTab = new JPanel(new BorderLayout(10, 10));
        
        // Summary Panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalPomodorosLabel = new JLabel("Total Pomodoro Sessions: 0");
        totalPomodorosLabel.setFont(new Font("Arial", Font.BOLD, 16));
        summaryPanel.add(totalPomodorosLabel);

        // Productivity Trends Panel
        JPanel trendsPanel = new JPanel(new GridLayout(2, 1));
        productiveHoursLabel = new JLabel("Most Productive Hours: N/A");
        productiveHoursLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        productiveDaysLabel = new JLabel("Most Productive Days: N/A");
        productiveDaysLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        trendsPanel.add(productiveHoursLabel);
        trendsPanel.add(productiveDaysLabel);
        summaryPanel.add(trendsPanel);

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

    /**
     * Creates a chart tab for specific time period statistics.
     *
     * @param title Chart title
     * @param unit Time unit for grouping data
     * @param periods Number of periods to display
     * @return Panel containing the chart
     */
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

    /**
     * Handles updates to analytics data.
     *
     * @param stats Updated task statistics
     * @param totalPomodoros Total number of completed Pomodoros
     */
    @Override
    public void onStatsUpdated(Map<String, TaskStats> stats, int totalPomodoros) {
        updateDisplay(stats, totalPomodoros);
    }

    /**
     * Updates all display components with new statistics.
     *
     * @param stats Task statistics to display
     * @param totalPomodoros Total number of completed Pomodoros
     */
    private void updateDisplay(Map<String, TaskStats> stats, int totalPomodoros) {
        totalPomodorosLabel.setText("Total Pomodoro Sessions: " + totalPomodoros);

        // Update table
        tableModel.setRowCount(0);
        stats.values().stream()
            .forEach(stat -> tableModel.addRow(new Object[]{
                stat.getTaskName(),
                stat.getFormattedTimeSpent()
            }));

        // Update productivity trends
        updateProductivityTrends(stats);

        // Update each chart in its tab
        updateChart(stats, "Daily Tasks", ChronoUnit.DAYS, DEFAULT_PERIODS);
        updateChart(stats, "Weekly Tasks", ChronoUnit.WEEKS, DEFAULT_PERIODS/7);
        updateChart(stats, "Monthly Tasks", ChronoUnit.MONTHS, DEFAULT_PERIODS/30);
    }

    /**
     * Updates productivity trend labels with most productive hours and days.
     *
     * @param stats Task statistics for analysis
     */
    private void updateProductivityTrends(Map<String, TaskStats> stats) {
        Map<Integer, Long> hourProductivity = new HashMap<>();
        Map<DayOfWeek, Long> dayProductivity = new HashMap<>();

        stats.values().forEach(stat -> {
            LocalDateTime completionTime = stat.getCompletionTime();
            if (completionTime != null) {
                int hour = completionTime.getHour();
                DayOfWeek day = completionTime.getDayOfWeek();

                hourProductivity.put(hour, hourProductivity.getOrDefault(hour, 0L) + 1);
                dayProductivity.put(day, dayProductivity.getOrDefault(day, 0L) + 1);
            }
        });

        int mostProductiveHour = hourProductivity.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(-1);

        DayOfWeek mostProductiveDay = dayProductivity.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);

        productiveHoursLabel.setText("Most Productive Hours: " + (mostProductiveHour != -1 ? mostProductiveHour + ":00" : "N/A"));
        productiveDaysLabel.setText("Most Productive Days: " + (mostProductiveDay != null ? mostProductiveDay : "N/A"));
    }

    /**
     * Updates specific chart with new statistics.
     *
     * @param stats Task statistics to display
     * @param title Chart title
     * @param unit Time unit for grouping data
     * @param periods Number of periods to display
     */
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

    /**
     * Recursively searches for chart holder panel by title.
     *
     * @param container Container to search in
     * @param title Title of the chart holder to find
     * @return Found chart holder panel or null
     */
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

    /**
     * Creates a chart panel with specified settings.
     *
     * @param chart Chart to display
     * @param preferredSize Preferred size for the panel
     * @return Configured chart panel
     */
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
        SlidingCategoryDataset slidingDataset = new SlidingCategoryDataset(baseDataset, 0, 30);

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

        // Setup navigation buttons
        JPanel chartHolder = findChartHolder(this, title);
        if (chartHolder != null) {
            JButton prevButton = (JButton)chartHolder.getClientProperty("prevButton");
            JButton nextButton = (JButton)chartHolder.getClientProperty("nextButton");
            JLabel pageLabel = (JLabel)chartHolder.getClientProperty("pageLabel");
            
            prevButton.addActionListener(e -> {
                int firstIdx = slidingDataset.getFirstCategoryIndex();
                if (firstIdx > 0) {
                    slidingDataset.setFirstCategoryIndex(Math.max(0, firstIdx - 30));
                    updatePageLabel(pageLabel, slidingDataset, baseDataset);
                    chart.fireChartChanged(); // Notify chart about data changes
                }
            });
            
            nextButton.addActionListener(e -> {
                int firstIdx = slidingDataset.getFirstCategoryIndex();
                int maxIdx = baseDataset.getColumnCount() - 30;
                if (firstIdx < maxIdx) {
                    slidingDataset.setFirstCategoryIndex(Math.min(maxIdx, firstIdx + 30));
                    updatePageLabel(pageLabel, slidingDataset, baseDataset);
                    chart.fireChartChanged(); // Notify chart about data changes
                }
            });
            
            // Initialize page label
            updatePageLabel(pageLabel, slidingDataset, baseDataset);
        }

        return chart;
    }

    private void updatePageLabel(JLabel label, SlidingCategoryDataset sliding, 
                               DefaultCategoryDataset base) {
        int currentPage = (sliding.getFirstCategoryIndex() / 30) + 1;
        int totalPages = (int)Math.ceil(base.getColumnCount() / 30.0);
        if (sliding.getFirstCategoryIndex() + 30 >= base.getColumnCount()) {
            currentPage = totalPages;
        }
        label.setText(String.format("Page %d of %d", currentPage, totalPages));
    }

    private String getPeriodLabel(LocalDateTime date, ChronoUnit unit) {
        return switch (unit) {
            case DAYS -> date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            case WEEKS -> "Week " + date.get(WeekFields.ISO.weekOfWeekBasedYear());
            case MONTHS -> date.format(DateTimeFormatter.ofPattern("MMM yyyy"));
            default -> date.toString();
        };
    }

    private String formatPeriodLabel(LocalDateTime date, ChronoUnit unit) {
        return getPeriodLabel(date, unit);
    }
}
