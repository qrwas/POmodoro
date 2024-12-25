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
    private int timeLeft = 25 * 60; // 25 minutes in seconds

    public MainPanel() {
        setLayout(new BorderLayout(10, 10));
        initializeComponents();
    }

    private void initializeComponents() {
        // Timer Panel
        JPanel timerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        timerLabel = new JLabel("25:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 48));
        
        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        resetButton = new JButton("Reset");
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 20, 10);
        timerPanel.add(timerLabel, gbc);
        
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        timerPanel.add(startButton, gbc);
        gbc.gridx = 1;
        timerPanel.add(stopButton, gbc);
        gbc.gridx = 2;
        timerPanel.add(resetButton, gbc);

        // Task Panel
        JPanel taskPanel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(
            new String[]{"Task", "Priority", "Status"}, 0
        );
        taskTable = new JTable(tableModel);
        
        JPanel taskButtonPanel = new JPanel();
        JButton addButton = new JButton("Add Task");
        JButton editButton = new JButton("Edit Task");
        JButton deleteButton = new JButton("Delete Task");
        
        taskButtonPanel.add(addButton);
        taskButtonPanel.add(editButton);
        taskButtonPanel.add(deleteButton);

        taskPanel.add(new JScrollPane(taskTable), BorderLayout.CENTER);
        taskPanel.add(taskButtonPanel, BorderLayout.SOUTH);

        // Add panels to main panel
        add(timerPanel, BorderLayout.NORTH);
        add(taskPanel, BorderLayout.CENTER);

        setupTimerActions();
        setupTaskActions(addButton, editButton, deleteButton);
    }

    private void setupTimerActions() {
        pomodoroTimer = new Timer(1000, e -> updateTimer());
        
        startButton.addActionListener(e -> pomodoroTimer.start());
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
            int minutes = timeLeft / 60;
            int seconds = timeLeft % 60;
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
        } else {
            pomodoroTimer.stop();
            JOptionPane.showMessageDialog(this, "Pomodoro session finished!");
            resetTimer();
        }
    }

    private void resetTimer() {
        timeLeft = 25 * 60;
        timerLabel.setText("25:00");
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
}
