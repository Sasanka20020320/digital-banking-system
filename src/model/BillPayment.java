package model;

import java.io.Serializable;
import java.time.Instant;

/*
Represents bill transactions and encapsulates bill-related data
such as type, amount, and timestamp.
 */

public class BillPayment implements Serializable {
    // Prevent Serialization errors
    private static final long serialVersionUID = 1L;

    private static int counter = 0;

    private int billId;
    private String billType;
    private double amount;
    private Instant timestamp;

    private long dueDate;

    public BillPayment(String billType, double amount) {
        this.billId = ++counter;
        this.billType = billType;
        this.amount = amount;
        this.timestamp = Instant.now();

        // due in 7 days
        this.dueDate = System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000);
    }

    // Getters
    public int getBillId() {
        return billId;
    }

    public String getBillType() {
        return billType;
    }

    public double getAmount() {
        return amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public boolean isDueSoon() {
        long now = System.currentTimeMillis();
        long diff = dueDate - now;

        // 2 days
        return diff <= (2L * 24 * 60 * 60 * 1000) && diff > 0;
    }

    @Override
    public String toString() {
        return "Bill ID: " + billId +
                ", Type: " + billType +
                ", Amount: " + amount +
                ", Time: " + timestamp;
    }
}

/*
 DESIGN DECISIONS & OOP PRINCIPLES:

 1. ENCAPSULATION:
    - Bill data is private and accessed via getters.

 2. SINGLE RESPONSIBILITY:
    - Represents a bill payment only.

 3. REAL-WORLD MODELING:
    - Includes due date and urgency detection (isDueSoon).

 4. EXTENSIBILITY:
    - Can support reminders or penalties later.
*/