package main.java.com.pomodoro.ui;

import main.java.com.pomodoro.model.Task;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;
import java.util.Comparator;
import java.util.ArrayList;

public class MainPanel extends JPanel {
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JLabel timerLabel;
    private JButton startButton;
    private JButton stopButton;
    private JButton resetButton;
    private Timer pomodoroTimer;
    private int workInterval = 25 * 60; // 25 minutes in seconds
    private int shortBreakInterval = 5 * 60; // 5 minutes in seconds
    private int longBreakInterval = 15 * 60; // 15 minutes in seconds
    private int timeLeft = workInterval;
    private boolean isWorkSession = true;
    private int currentActiveTask = -1; // Add this field to track active task
    private JTextField shortBreakField; // Add this field
    private java.util.List<Task> tasks = new ArrayList<>(); // Add this field
    private int sessionsUntilLongBreak = 4; // Default value
    private int completedSessions = 0;
    private JTextField sessionsField;
    private JTextField longBreakField;
    private ButtonGroup filterGroup;
    private DefaultTableModel originalTableModel;

    public MainPanel() {
        setLayout(new BorderLayout(10, 10));
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Main container for timer and settings
        JPanel topContainer = new JPanel(new BorderLayout(10, 10));
        
        // Timer Panel
        JPanel timerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        timerLabel = new JLabel(formatTime(timeLeft));
        timerLabel.setFont(new Font("Arial", Font.BOLD, 48));
        
        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        resetButton = new JButton("Reset");
        
        // Timer display
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 20, 10);
        timerPanel.add(timerLabel, gbc);
        
        // Timer buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(resetButton);
        
        gbc.gridy = 1;
        timerPanel.add(buttonPanel, gbc);
        
        // Interval Settings Panel
        JPanel intervalPanel = new JPanel(new GridLayout(4, 2, 10, 10)); // Changed to 4 rows
        intervalPanel.setBorder(BorderFactory.createTitledBorder("Interval Settings"));
        
        JLabel workLabel = new JLabel("Work Interval (minutes):");
        JLabel shortBreakLabel = new JLabel("Short Break Interval (minutes):");
        JLabel longBreakLabel = new JLabel("Long Break Interval (minutes):");
        JLabel sessionsLabel = new JLabel("Sessions until long break:");
        
        JTextField workField = new JTextField("25");
        shortBreakField = new JTextField("5"); // Store reference to field
        longBreakField = new JTextField("15");
        sessionsField = new JTextField("4");
        
        intervalPanel.add(workLabel);
        intervalPanel.add(workField);
        intervalPanel.add(shortBreakLabel);
        intervalPanel.add(shortBreakField);
        intervalPanel.add(longBreakLabel);
        intervalPanel.add(longBreakField);
        intervalPanel.add(sessionsLabel);
        intervalPanel.add(sessionsField);

        // Add timer and settings to top container
        topContainer.add(timerPanel, BorderLayout.NORTH);
        topContainer.add(intervalPanel, BorderLayout.CENTER);

        // Task Panel with sorting
        JPanel taskPanel = new JPanel(new BorderLayout(0, 10));
        tableModel = new DefaultTableModel(
            new String[]{"Task", "Priority", "Status"}, 0
        );
        taskTable = new JTable(tableModel);
        
        // Task control panel with sort and filter buttons
        JPanel taskControlPanel = new JPanel(new BorderLayout());
        JPanel leftControlPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JPanel taskButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        // Sort buttons
        JButton sortByNameButton = new JButton("Sort by Name");
        JButton sortByPriorityButton = new JButton("Sort by Priority");
        
        sortPanel.add(new JLabel("Sort: "));
        sortPanel.add(sortByNameButton);
        sortPanel.add(sortByPriorityButton);

        // Filter buttons
        filterPanel.add(new JLabel("Filter: "));
        filterGroup = new ButtonGroup();
        JRadioButton allTasksButton = new JRadioButton("All", true);
        JRadioButton activeTasksButton = new JRadioButton("Active");
        JRadioButton completedTasksButton = new JRadioButton("Completed");
        
        filterGroup.add(allTasksButton);
        filterGroup.add(activeTasksButton);
        filterGroup.add(completedTasksButton);
        
        filterPanel.add(allTasksButton);
        filterPanel.add(activeTasksButton);
        filterPanel.add(completedTasksButton);

        leftControlPanel.add(sortPanel);
        leftControlPanel.add(filterPanel);

        // Task buttons
        JButton addButton = new JButton("Add Task");
        JButton editButton = new JButton("Edit Task");
        JButton deleteButton = new JButton("Delete Task");
        
        taskButtonPanel.add(addButton);
        taskButtonPanel.add(editButton);
        taskButtonPanel.add(deleteButton);

        taskControlPanel.add(leftControlPanel, BorderLayout.WEST);
        taskControlPanel.add(taskButtonPanel, BorderLayout.EAST);
        
        taskPanel.add(new JScrollPane(taskTable), BorderLayout.CENTER);
        taskPanel.add(taskControlPanel, BorderLayout.SOUTH);

        // Add everything to main panel
        add(topContainer, BorderLayout.NORTH);
        add(taskPanel, BorderLayout.CENTER);

        setupTimerActions();
        setupTaskActions(addButton, editButton, deleteButton);
        setupSortActions(sortByNameButton, sortByPriorityButton);

        // Interval settings actions
        workField.addActionListener(e -> workInterval = Integer.parseInt(workField.getText()) * 60);
        shortBreakField.addActionListener(e -> shortBreakInterval = Integer.parseInt(shortBreakField.getText()) * 60);
        longBreakField.addActionListener(e -> longBreakInterval = Integer.parseInt(longBreakField.getText()) * 60);
        sessionsField.addActionListener(e -> sessionsUntilLongBreak = Integer.parseInt(sessionsField.getText()));

        // Add filter actions
        allTasksButton.addActionListener(e -> filterTasks("All"));
        activeTasksButton.addActionListener(e -> filterTasks("Active"));
        completedTasksButton.addActionListener(e -> filterTasks("Completed"));
    }

    private void setupTimerActions() {
        pomodoroTimer = new Timer(1000, e -> updateTimer());
        
        startButton.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow != -1) {
                Task selectedTask = tasks.get(selectedRow);
                if (selectedTask.isCompleted()) {
                    JOptionPane.showMessageDialog(this, "Cannot start a completed task!");
                    return;
                }
                if (currentActiveTask != -1 && currentActiveTask != selectedRow) {
                    JOptionPane.showMessageDialog(this, "Another task is already in progress!");
                    return;
                }
                currentActiveTask = selectedRow;
                updateTaskStatus(selectedRow, "In Progress");
                pomodoroTimer.start();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a task to start.");
            }
        });

        stopButton.addActionListener(e -> {
            if (currentActiveTask != -1) {
                updateTaskStatus(currentActiveTask, "Paused");
                pomodoroTimer.stop();
            }
        });

        resetButton.addActionListener(e -> {
            if (currentActiveTask != -1) {
                updateTaskStatus(currentActiveTask, "Not Started");
                currentActiveTask = -1;
            }
            resetTimer();
        });
    }

    private void setupTaskActions(JButton addButton, JButton editButton, JButton deleteButton) {
        addButton.addActionListener(e -> addNewTask());
        editButton.addActionListener(e -> editSelectedTask());
        deleteButton.addActionListener(e -> deleteSelectedTask());
    }

    private void setupSortActions(JButton sortByNameButton, JButton sortByPriorityButton) {
        sortByNameButton.addActionListener(e -> sortTasks(0)); // 0 for name column
        sortByPriorityButton.addActionListener(e -> sortTasks(1)); // 1 for priority column
    }

    private void sortTasks(int columnIndex) {
        // First, sort tasks list
        tasks.sort((task1, task2) -> {
            // Always put completed tasks at the bottom
            if (task1.isCompleted() && !task2.isCompleted()) return 1;
            if (!task1.isCompleted() && task2.isCompleted()) return -1;
            
            // If both tasks have same completion status, sort by column
            if (columnIndex == 0) {
                return task1.getName().compareTo(task2.getName());
            } else if (columnIndex == 1) {
                return Integer.compare(task1.getPriority(), task2.getPriority());
            }
            return 0;
        });

        // Update table to reflect new order
        updateTableFromTasks();
    }

    private void updateTableFromTasks() {
        tableModel.setRowCount(0);
        for (Task task : tasks) {
            tableModel.addRow(new Object[]{
                task.getName(),
                getPriorityLabel(task.getPriority()),
                getTaskStatus(task)
            });
        }
    }

    private void filterTasks(String filterType) {
        tableModel.setRowCount(0);
        for (Task task : tasks) {
            boolean shouldAdd = switch (filterType) {
                case "All" -> true;
                case "Active" -> !task.isCompleted();
                case "Completed" -> task.isCompleted();
                default -> true;
            };
            
            if (shouldAdd) {
                tableModel.addRow(new Object[]{
                    task.getName(),
                    getPriorityLabel(task.getPriority()),
                    getTaskStatus(task)
                });
            }
        }
    }

    private String getTaskStatus(Task task) {
        if (task.isCompleted()) return "Completed";
        if (task.isInProgress()) return "In Progress";
        return "Not Started";
    }

    private void updateTimer() {
        if (timeLeft > 0) {
            timeLeft--;
            timerLabel.setText(formatTime(timeLeft));
        } else {
            pomodoroTimer.stop();
            if (isWorkSession) {
                if (currentActiveTask != -1) {
                    updateTaskStatus(currentActiveTask, "Completed");
                    currentActiveTask = -1;
                    completedSessions++;
                }
                
                // Determine break type
                boolean isLongBreak = completedSessions >= sessionsUntilLongBreak;
                if (isLongBreak) {
                    completedSessions = 0; // Reset counter after long break
                }
                
                int breakDuration = isLongBreak ? 
                    Integer.parseInt(longBreakField.getText()) * 60 : 
                    Integer.parseInt(shortBreakField.getText()) * 60;
                
                // Show break dialog
                BreakDialog breakDialog = new BreakDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    breakDuration,
                    isLongBreak
                );
                breakDialog.setVisible(true);
                
                if (breakDialog.wasBreakSkipped()) {
                    timeLeft = workInterval;
                    isWorkSession = true;
                } else {
                    timeLeft = workInterval;
                    isWorkSession = true;
                }
            } else {
                timeLeft = workInterval;
                isWorkSession = true;
            }
            timerLabel.setText(formatTime(timeLeft));
        }
    }

    private void resetTimer() {
        timeLeft = isWorkSession ? workInterval : shortBreakInterval;
        timerLabel.setText(formatTime(timeLeft));
        pomodoroTimer.stop();
    }

    private void addNewTask() {
        String name = JOptionPane.showInputDialog(this, "Enter task name:");
        if (name != null && !name.trim().isEmpty()) {
            String[] priorityLabels = {"High", "Medium", "Low"};
            String selectedPriority = (String) JOptionPane.showInputDialog(
                this,
                "Select priority:",
                "Task Priority",
                JOptionPane.QUESTION_MESSAGE,
                null,
                priorityLabels,
                priorityLabels[1]
            );
            if (selectedPriority != null) {
                int priorityValue = getPriorityValue(selectedPriority);
                Task task = new Task(name, priorityValue);
                tasks.add(task);
                tableModel.addRow(new Object[]{
                    task.getName(), 
                    getPriorityLabel(task.getPriority()), 
                    task.isCompleted() ? "Completed" : "Not Started"
                });
            }
        }
    }

    private void editSelectedTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow != -1) {
            Task task = tasks.get(selectedRow);
            String name = JOptionPane.showInputDialog(this, "Enter new task name:", task.getName());
            if (name != null && !name.trim().isEmpty()) {
                task.setName(name);
                tableModel.setValueAt(name, selectedRow, 0);
            }
        }
    }

    private void deleteSelectedTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow != -1) {
            Task selectedTask = tasks.get(selectedRow);
            if (selectedTask.isInProgress()) {
                JOptionPane.showMessageDialog(this, "Cannot delete a task that is in progress!");
                return;
            }
            if (selectedRow == currentActiveTask) {
                JOptionPane.showMessageDialog(this, "Cannot delete the active task!");
                return;
            }
            tasks.remove(selectedRow);
            tableModel.removeRow(selectedRow);
        }
    }

    private int getPriorityValue(String priority) {
        switch (priority) {
            case "High": return 1;
            case "Medium": return 2;
            case "Low": return 3;
            default: return 4;
        }
    }

    private String getPriorityLabel(int priority) {
        switch (priority) {
            case 1: return "High";
            case 2: return "Medium";
            case 3: return "Low";
            default: return "Unknown";
        }
    }

    private void updateTaskStatus(int row, String status) {
        if (row >= 0 && row < tasks.size()) {
            Task task = tasks.get(row);
            switch (status) {
                case "In Progress":
                    task.setInProgress(true);
                    task.setCompleted(false);
                    break;
                case "Completed":
                    task.setInProgress(false);
                    task.setCompleted(true);
                    break;
                case "Paused":
                    task.setInProgress(false);
                    break;
                case "Not Started":
                    task.setInProgress(false);
                    task.setCompleted(false);
                    break;
            }
            tableModel.setValueAt(status, row, 2);
        }
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
}
