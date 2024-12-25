package main.java.com.pomodoro.ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

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
        JPanel intervalPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        intervalPanel.setBorder(BorderFactory.createTitledBorder("Interval Settings"));
        
        JLabel workLabel = new JLabel("Work Interval (minutes):");
        JLabel shortBreakLabel = new JLabel("Short Break Interval (minutes):");
        JLabel longBreakLabel = new JLabel("Long Break Interval (minutes):");
        
        JTextField workField = new JTextField("25");
        JTextField shortBreakField = new JTextField("5");
        JTextField longBreakField = new JTextField("15");
        
        intervalPanel.add(workLabel);
        intervalPanel.add(workField);
        intervalPanel.add(shortBreakLabel);
        intervalPanel.add(shortBreakField);
        intervalPanel.add(longBreakLabel);
        intervalPanel.add(longBreakField);

        // Add timer and settings to top container
        topContainer.add(timerPanel, BorderLayout.NORTH);
        topContainer.add(intervalPanel, BorderLayout.CENTER);

        // Task Panel
        JPanel taskPanel = new JPanel(new BorderLayout(0, 10));
        tableModel = new DefaultTableModel(
            new String[]{"Task", "Priority", "Status"}, 0
        );
        taskTable = new JTable(tableModel);
        
        JPanel taskButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton addButton = new JButton("Add Task");
        JButton editButton = new JButton("Edit Task");
        JButton deleteButton = new JButton("Delete Task");
        
        taskButtonPanel.add(addButton);
        taskButtonPanel.add(editButton);
        taskButtonPanel.add(deleteButton);

        taskPanel.add(new JScrollPane(taskTable), BorderLayout.CENTER);
        taskPanel.add(taskButtonPanel, BorderLayout.SOUTH);

        // Add everything to main panel
        add(topContainer, BorderLayout.NORTH);
        add(taskPanel, BorderLayout.CENTER);

        setupTimerActions();
        setupTaskActions(addButton, editButton, deleteButton);

        // Interval settings actions
        workField.addActionListener(e -> workInterval = Integer.parseInt(workField.getText()) * 60);
        shortBreakField.addActionListener(e -> shortBreakInterval = Integer.parseInt(shortBreakField.getText()) * 60);
        longBreakField.addActionListener(e -> longBreakInterval = Integer.parseInt(longBreakField.getText()) * 60);
    }

    private void setupTimerActions() {
        pomodoroTimer = new Timer(1000, e -> updateTimer());
        
        startButton.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.setValueAt("In Progress", selectedRow, 2);
                pomodoroTimer.start();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a task to start.");
            }
        });
        stopButton.addActionListener(e -> pomodoroTimer.stop());
        resetButton.addActionListener(e -> resetTimer());
    }

    private void setupTaskActions(JButton addButton, JButton editButton, JButton deleteButton) {
        addButton.addActionListener(e -> addNewTask());
        editButton.addActionListener(e -> editSelectedTask());
        deleteButton.addActionListener(e -> deleteSelectedTask());
    }

    private void updateTimer() {
        if (timeLeft > 0) {
            timeLeft--;
            timerLabel.setText(formatTime(timeLeft));
        } else {
            pomodoroTimer.stop();
            if (isWorkSession) {
                JOptionPane.showMessageDialog(this, "Work session finished! Time for a break.");
                timeLeft = shortBreakInterval;
                isWorkSession = false;
            } else {
                JOptionPane.showMessageDialog(this, "Break finished! Time to work.");
                timeLeft = workInterval;
                isWorkSession = true;
            }
            timerLabel.setText(formatTime(timeLeft));
            pomodoroTimer.start();
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
            String[] priority = {"High", "Medium", "Low"};
            String selectedPriority = (String) JOptionPane.showInputDialog(
                this,
                "Select priority:",
                "Task Priority",
                JOptionPane.QUESTION_MESSAGE,
                null,
                priority,
                priority[1]
            );
            if (selectedPriority != null) {
                tableModel.addRow(new Object[]{name, selectedPriority, "Not Started"});
            }
        }
    }

    private void editSelectedTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow != -1) {
            String name = JOptionPane.showInputDialog(this, "Enter new task name:", 
                taskTable.getValueAt(selectedRow, 0));
            if (name != null && !name.trim().isEmpty()) {
                tableModel.setValueAt(name, selectedRow, 0);
            }
        }
    }

    private void deleteSelectedTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow);
        }
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
}
