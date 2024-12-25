package main.java.com.pomodoro.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class BreakDialog extends JDialog {
    private JLabel timerLabel;
    private Timer breakTimer;
    private int timeLeft;
    private boolean breakSkipped = false;

    public BreakDialog(JFrame parent, int breakDuration, boolean isLongBreak) {
        super(parent, "Break Time!", true);
        this.timeLeft = breakDuration;
        
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

    private void updateTimer() {
        if (timeLeft > 0) {
            timeLeft--;
            timerLabel.setText(formatTime(timeLeft));
        } else {
            breakTimer.stop();
            dispose();
        }
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    public boolean wasBreakSkipped() {
        return breakSkipped;
    }
}
