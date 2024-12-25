package com.pomodoro.service;

import javafx.scene.media.AudioClip;

public class SoundService {
    private static final String TIMER_END_SOUND = "/sounds/timer-end.wav";
    private static final String BREAK_END_SOUND = "/sounds/break-end.wav";
    
    private final AudioClip timerEndSound;
    private final AudioClip breakEndSound;
    private boolean soundEnabled = true;

    public SoundService() {
        timerEndSound = new AudioClip(getClass().getResource(TIMER_END_SOUND).toExternalForm());
        breakEndSound = new AudioClip(getClass().getResource(BREAK_END_SOUND).toExternalForm());
    }

    public void playTimerEndSound() {
        if (soundEnabled) {
            timerEndSound.play();
        }
    }

    public void playBreakEndSound() {
        if (soundEnabled) {
            breakEndSound.play();
        }
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }
}
