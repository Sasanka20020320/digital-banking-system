package model;

import java.time.Instant;
import java.io.Serializable;

public class Notification implements Serializable {
    // Prevent Serialization errors
    private static final long serialVersionUID = 1L;

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
