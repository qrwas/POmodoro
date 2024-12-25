package com.pomodoro.controller;

import com.pomodoro.model.Task;
import com.pomodoro.model.PomodoroSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

public class MainController {
    @FXML private Label timerLabel;
    @FXML private Button startButton;
    @FXML private Button pauseButton;
    @FXML private Button resetButton;
    @FXML private TextField taskInput;
    @FXML private ComboBox<Task.Priority> priorityComboBox;
    @FXML private ListView<Task> taskListView;
    @FXML private ProgressBar dailyProgress;
    @FXML private Label completedPomodorosLabel;
    @FXML private LineChart<String, Number> productivityChart;

    private Timeline timer;
    private int timeSeconds;
    private PomodoroSession currentSession;
    private SoundService soundService;
    private TaskStorage taskStorage;

    @FXML
    public void initialize() {
        priorityComboBox.getItems().addAll(Task.Priority.values());
        pauseButton.setDisable(true);
        resetButton.setDisable(true);
        
        setupTimer();
        soundService = new SoundService();
        taskStorage = new TaskStorage();
        loadSavedTasks();
        setupProductivityChart();
    }

    private void setupTimer() {
        timeSeconds = 25 * 60; // 25 minutes
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeSeconds--;
            updateTimerLabel();
            if (timeSeconds <= 0) {
                timer.stop();
                handlePomodoroComplete();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
    }

    private void updateTimerLabel() {
        int minutes = timeSeconds / 60;
        int seconds = timeSeconds % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    @FXML
    private void handleStartButton() {
        timer.play();
        startButton.setDisable(true);
        pauseButton.setDisable(false);
        resetButton.setDisable(false);
    }

    @FXML
    private void handlePauseButton() {
        timer.pause();
        startButton.setDisable(false);
        pauseButton.setDisable(true);
    }

    @FXML
    private void handleResetButton() {
        timer.stop();
        timeSeconds = 25 * 60;
        updateTimerLabel();
        startButton.setDisable(false);
        pauseButton.setDisable(true);
        resetButton.setDisable(true);
    }

    @FXML
    private void handleAddTask() {
        String taskName = taskInput.getText().trim();
        Task.Priority priority = priorityComboBox.getValue();
        
        if (!taskName.isEmpty() && priority != null) {
            Task task = new Task(taskName, priority);
            taskListView.getItems().add(task);
            taskInput.clear();
            priorityComboBox.setValue(null);
        }
    }

    @FXML
    private void handleEditTask() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            TextInputDialog dialog = new TextInputDialog(selectedTask.getName());
            dialog.setTitle("Редагування завдання");
            dialog.setHeaderText(null);
            dialog.setContentText("Введіть нову назву завдання:");

            dialog.showAndWait().ifPresent(newName -> {
                // Оновлюємо назву завдання
                selectedTask.setName(newName);
                taskListView.refresh();
                taskStorage.saveTasks(taskListView.getItems());
            });
        }
    }

    @FXML
    private void handleDeleteTask() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            taskListView.getItems().remove(selectedTask);
            taskStorage.saveTasks(taskListView.getItems());
        }
    }

    private void loadSavedTasks() {
        taskListView.getItems().addAll(taskStorage.loadTasks());
    }

    private void setupProductivityChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Щоденна продуктивність");
        // Додаємо тестові дані
        series.getData().add(new XYChart.Data<>("Пн", 4));
        series.getData().add(new XYChart.Data<>("Вт", 6));
        series.getData().add(new XYChart.Data<>("Ср", 3));
        productivityChart.getData().add(series);
    }

    private void handlePomodoroComplete() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pomodoro Complete");
        alert.setHeaderText(null);
        alert.setContentText("Time for a break!");
        alert.showAndWait();
        soundService.playTimerEndSound();
        updateProductivityChart();
    }

    private void updateProductivityChart() {
        // Оновлюємо графік після завершення помодоро
        XYChart.Series<String, Number> series = productivityChart.getData().get(0);
        String today = LocalDateTime.now().getDayOfWeek().getDisplayName(
            TextStyle.SHORT, new Locale("uk", "UA"));
        
        series.getData().add(new XYChart.Data<>(today, 
            Integer.parseInt(completedPomodorosLabel.getText()) + 1));
    }
}
