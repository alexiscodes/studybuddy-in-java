package com.studybuddy.service;

public class PomodoroTimer {
    private static final int DEFAULT_MINUTES = 25;
    private int remainingSeconds = DEFAULT_MINUTES * 60;

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public boolean tick() {
        if (remainingSeconds > 0) {
            remainingSeconds--;
        }
        return remainingSeconds ==0;
    }

    public void reset() {
        remainingSeconds = DEFAULT_MINUTES * 60;    
    }

    public String getFormattedTime() {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}