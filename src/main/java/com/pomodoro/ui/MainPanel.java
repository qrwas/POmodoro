package main.java.com.pomodoro.ui;

import main.java.com.pomodoro.model.Task;
import main.java.com.pomodoro.service.TaskManager;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class MainPanel extends JPanel implements TaskManager.TaskChangeListener {
    private final TaskManager taskManager;
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
    private int sessionsUntilLongBreak = 4; // Default value
    private int completedSessions = 0;
    private ButtonGroup filterGroup;
    private String currentFilter = "All"; // Add this field

    public MainPanel(TaskManager taskManager) {
        this.taskManager = taskManager;
        taskManager.addListener(this);
        setLayout(new BorderLayout(10, 10));
        initializeComponents();
        updateTableFromTasks(); // Add initial table update
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
        
        // Add timer and settings to top container
        topContainer.add(timerPanel, BorderLayout.NORTH);

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

    // Method to update intervals from SettingsDialog
    public void updateIntervals(int workMins, int shortBreakMins, int longBreakMins, int sessions) {
        this.workInterval = workMins * 60;
        this.shortBreakInterval = shortBreakMins * 60;
        this.longBreakInterval = longBreakMins * 60;
        this.sessionsUntilLongBreak = sessions;
        
        if (!pomodoroTimer.isRunning()) {
            timeLeft = isWorkSession ? workInterval : shortBreakInterval;
            timerLabel.setText(formatTime(timeLeft));
        }
    }

    private void setupTimerActions() {
        pomodoroTimer = new Timer(1000, e -> updateTimer());
        
        startButton.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow != -1) {
                Task selectedTask = getTaskForTableRow(selectedRow);
                try {
                    taskManager.startTask(selectedTask, workInterval);
                    currentTask = selectedTask;
                    timeLeft = workInterval;
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
            if (isWorkSession && currentTask != null) {
                taskManager.completeTask(currentTask);
                currentTask = null;
                completedSessions++;
                
                boolean isLongBreak = completedSessions >= sessionsUntilLongBreak;
                if (isLongBreak) {
                    completedSessions = 0;
                }
                
                int breakDuration = isLongBreak ? longBreakInterval : shortBreakInterval;
                
                BreakDialog breakDialog = new BreakDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    breakDuration,
                    isLongBreak
                );
                breakDialog.setVisible(true);
                
                timeLeft = workInterval;
                isWorkSession = true;
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

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
}
