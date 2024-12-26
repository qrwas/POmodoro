package com.pomodoro.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit; // Додаємо імпорт для системних звуків

/**
 * Dialog window that appears during break intervals.
 * Displays a countdown timer and allows users to skip breaks.
 */
public class BreakDialog extends JDialog {
    private JLabel timerLabel;
    private Timer breakTimer;
    private int timeLeft;
    private boolean breakSkipped = false;

    /**
     * Creates a new break dialog.
     *
     * @param parent The parent frame for this dialog
     * @param breakDuration Duration of the break in seconds
     * @param isLongBreak True if this is a long break, false for short break
     */
    public BreakDialog(JFrame parent, int breakDuration, boolean isLongBreak) {
        super(parent, "Break Time!", true);
        this.timeLeft = breakDuration;
        
        // Play start break sound
        Toolkit.getDefaultToolkit().beep();
        
        setLayout(new BorderLayout(10, 10));
        
        // Timer display
        timerLabel = new JLabel(formatTime(timeLeft), SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 48));
        
        // Message
        String messageText = isLongBreak ? 
            "Time for a long break! Take a walk or do some exercise." :
            "Time for a short break! Relax and stretch.";
        JLabel messageLabel = new JLabel(messageText, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        // Skip button
        JButton skipButton = new JButton("Skip Break");
        skipButton.addActionListener(e -> {
            breakSkipped = true;
            breakTimer.stop();
            dispose();
        });
        
        // Layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(messageLabel, BorderLayout.NORTH);
        mainPanel.add(timerLabel, BorderLayout.CENTER);
        mainPanel.add(skipButton, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Configure dialog
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        // Setup timer
        breakTimer = new Timer(1000, e -> updateTimer());
        breakTimer.start();
    }

    /**
     * Updates the countdown timer display.
     * Closes the dialog when timer reaches zero.
     */
    private void updateTimer() {
        if (timeLeft > 0) {
            timeLeft--;
            timerLabel.setText(formatTime(timeLeft));
        } else {
            breakTimer.stop();
            // Play end break sound
            Toolkit.getDefaultToolkit().beep();
            dispose();
        }
    }

    /**
     * Formats time in seconds to MM:SS format.
     *
     * @param seconds Time to format in seconds
     * @return Formatted time string
     */
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    /**
     * Checks if the break was skipped by the user.
     *
     * @return True if break was skipped, false otherwise
     */
    public boolean wasBreakSkipped() {
        return breakSkipped;
    }
}
