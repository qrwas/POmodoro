package main.java.com.pomodoro.ui;

import main.java.com.pomodoro.model.Task;
import main.java.com.pomodoro.service.TaskManager;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

public class MainPanel extends JPanel implements TaskManager.TaskChangeListener {
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
    private Task currentTask = null; // Replace currentActiveTask with Task reference
    private JTextField shortBreakField; // Add this field
    private java.util.List<Task> tasks = new ArrayList<>(); // Add this field
    private int sessionsUntilLongBreak = 4; // Default value
    private int completedSessions = 0;
    private JTextField sessionsField;
    private JTextField longBreakField;
    private ButtonGroup filterGroup;
    private DefaultTableModel originalTableModel;
    private String currentFilter = "All"; // Add this field
    private TaskManager taskManager;

    public MainPanel() {
        taskManager = new TaskManager();
        taskManager.addListener(this);
        setLayout(new BorderLayout(10, 10));
        initializeComponents();
    }

    @Override
    public void onTaskListChanged() {
        updateTableFromTasks();
    }

    @Override
    public void onTaskStatusChanged(Task task) {
        updateTableFromTasks();
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

        // Add filter actions with currentFilter tracking
        allTasksButton.addActionListener(e -> {
            currentFilter = "All";
            filterTasks(currentFilter);
        });
        activeTasksButton.addActionListener(e -> {
            currentFilter = "Active";
            filterTasks(currentFilter);
        });
        completedTasksButton.addActionListener(e -> {
            currentFilter = "Completed";
            filterTasks(currentFilter);
        });
    }

    private void setupTimerActions() {
        pomodoroTimer = new Timer(1000, e -> updateTimer());
        
        startButton.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow != -1) {
                Task selectedTask = getTaskForTableRow(selectedRow);
                try {
                    taskManager.startTask(selectedTask);
                    currentTask = selectedTask;
                    pomodoroTimer.start();
                } catch (IllegalStateException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a task to start.");
            }
        });

        stopButton.addActionListener(e -> {
            if (currentTask != null) {
                taskManager.pauseTask(currentTask);
                pomodoroTimer.stop();
            }
        });

        resetButton.addActionListener(e -> {
            if (currentTask != null) {
                taskManager.resetTask(currentTask);
                currentTask = null;
            }
            resetTimer();
        });
    }

    private Task getTaskForTableRow(int row) {
        if (row >= 0) {
            List<Task> filteredTasks = taskManager.getFilteredTasks(currentFilter);
            if (row < filteredTasks.size()) {
                return filteredTasks.get(row);
            }
        }
        return null;
    }

    private void setupTaskActions(JButton addButton, JButton editButton, JButton deleteButton) {
        addButton.addActionListener(e -> addNewTask());
        editButton.addActionListener(e -> editSelectedTask());
        deleteButton.addActionListener(e -> deleteSelectedTask());
    }

    private void setupSortActions(JButton sortByNameButton, JButton sortByPriorityButton) {
        sortByNameButton.addActionListener(e -> {
            taskManager.sortTasks(0);
            updateTableFromTasks();
        });
        sortByPriorityButton.addActionListener(e -> {
            taskManager.sortTasks(1);
            updateTableFromTasks();
        });
    }

    private void updateTableFromTasks() {
        tableModel.setRowCount(0);
        List<Task> tasksToShow = taskManager.getFilteredTasks(currentFilter);
        for (Task task : tasksToShow) {
            tableModel.addRow(new Object[]{
                task.getName(),
                taskManager.getPriorityLabel(task.getPriority()),
                taskManager.getTaskStatus(task)
            });
        }
    }

    private void filterTasks(String filterType) {
        updateTableFromTasks();
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
                taskManager.addTask(name, taskManager.getPriorityValue(selectedPriority));
            }
        }
    }

    private void editSelectedTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow != -1) {
            String taskName = (String) tableModel.getValueAt(selectedRow, 0);
            Task task = taskManager.findTaskByName(taskName);
            if (task != null) {
                String newName = JOptionPane.showInputDialog(this, "Enter new task name:", task.getName());
                if (newName != null && !newName.trim().isEmpty()) {
                    taskManager.updateTask(task, newName, task.getPriority());
                }
            }
        }
    }

    private void deleteSelectedTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow != -1) {
            String taskName = (String) tableModel.getValueAt(selectedRow, 0);
            Task task = taskManager.findTaskByName(taskName);
            if (task != null) {
                try {
                    taskManager.deleteTask(task);
                } catch (IllegalStateException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            }
        }
    }

    private void updateTimer() {
        if (timeLeft > 0) {
            timeLeft--;
            timerLabel.setText(formatTime(timeLeft));
        } else {
            pomodoroTimer.stop();
            if (isWorkSession) {
                if (currentTask != null) {
                    taskManager.completeTask(currentTask);
                    currentTask = null;
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

    private void updateTaskStatus(Task task, String status) {
        if (task != null) {
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
            filterTasks(currentFilter);
        }
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
}
