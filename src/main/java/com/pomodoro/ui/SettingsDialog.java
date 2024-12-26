package com.pomodoro.ui;

import com.pomodoro.model.Settings;
import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class SettingsDialog extends JDialog {
    private JTextField workField;
    private JTextField shortBreakField;
    private JTextField longBreakField;
    private JTextField sessionsField;
    private final Consumer<Settings> onSave;

    public SettingsDialog(JFrame parent, Settings currentSettings, Consumer<Settings> onSave) {
        super(parent, "Settings", true);
        this.onSave = onSave;

        setLayout(new BorderLayout(10, 10));
        
        // Settings Panel
        JPanel settingsPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        workField = new JTextField(String.valueOf(currentSettings.getWorkInterval() / 60));
        shortBreakField = new JTextField(String.valueOf(currentSettings.getShortBreakInterval() / 60));
        longBreakField = new JTextField(String.valueOf(currentSettings.getLongBreakInterval() / 60));
        sessionsField = new JTextField(String.valueOf(currentSettings.getSessionsUntilLongBreak()));
        
        settingsPanel.add(new JLabel("Work Interval (minutes):"));
        settingsPanel.add(workField);
        settingsPanel.add(new JLabel("Short Break Interval (minutes):"));
        settingsPanel.add(shortBreakField);
        settingsPanel.add(new JLabel("Long Break Interval (minutes):"));
        settingsPanel.add(longBreakField);
        settingsPanel.add(new JLabel("Sessions until long break:"));
        settingsPanel.add(sessionsField);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> saveSettings());
        cancelButton.addActionListener(e -> dispose());
        
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);

        add(settingsPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }

    private void saveSettings() {
        try {
            Settings settings = new Settings(
                Integer.parseInt(workField.getText()) * 60,
                Integer.parseInt(shortBreakField.getText()) * 60,
                Integer.parseInt(longBreakField.getText()) * 60,
                Integer.parseInt(sessionsField.getText())
            );
            onSave.accept(settings);
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numbers for all fields", 
                "Invalid Input", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
