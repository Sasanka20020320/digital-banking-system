package model;

import java.time.Instant;

public class Notification {
    private static int counter = 0;

    private int notificationId;
    private String message;
    private Instant timestamp;

    public Notification(String message) {
        this.notificationId = ++counter;
        this.message = message;
        this.timestamp = Instant.now();
    }

    // Getters
    public String getMessage() {
        return message;
    }

    public Instant getTimestamp()  {
        return timestamp;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + message;
    }
}
