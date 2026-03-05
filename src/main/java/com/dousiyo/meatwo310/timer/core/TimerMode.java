package com.dousiyo.meatwo310.timer.core;

public enum TimerMode {
    COUNTDOWN,
    COUNTUP;

    public static TimerMode fromCommand(String value) {
        if ("countdown".equalsIgnoreCase(value)) {
            return COUNTDOWN;
        }
        if ("countup".equalsIgnoreCase(value)) {
            return COUNTUP;
        }
        throw new IllegalArgumentException("Unknown timer mode: " + value);
    }
}
