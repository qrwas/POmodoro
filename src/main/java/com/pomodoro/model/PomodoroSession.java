package com.pomodoro.model;

import java.time.LocalDateTime;

public class PomodoroSession {
    private String id;
    private Task task;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SessionType sessionType;
    private SessionStatus status;

    public enum SessionType {
        WORK(25), SHORT_BREAK(5), LONG_BREAK(15);

        private final int defaultDuration;

        SessionType(int defaultDuration) {
            this.defaultDuration = defaultDuration;
        }

        public int getDefaultDuration() {
            return defaultDuration;
        }
    }

    public enum SessionStatus {
        RUNNING, PAUSED, COMPLETED
    }

    public PomodoroSession(Task task, SessionType sessionType) {
        this.id = java.util.UUID.randomUUID().toString();
        this.task = task;
        this.sessionType = sessionType;
        this.status = SessionStatus.RUNNING;
        this.startTime = LocalDateTime.now();
    }

    // Getters and setters
    // ... Standard getters and setters for all fields ...
}
