package model;

import java.time.Instant;
import java.io.Serializable;

public class Transaction implements Serializable {
    // Prevent Serialization errors
    private static final long serialVersionUID = 1L;

    private static int counter = 0;

    private int transactionId;
    private double amount;
    private TransactionType type;
    private Instant timestamp;

    public Transaction(double amount, TransactionType type) {
        this.transactionId = ++counter; // Auto-increment ID
        this.amount = amount;
        this.type = type;
        this.timestamp = Instant.now();
    }

    // Getters for private attributes
    public int getTransactionId() {
        return transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ID: " + transactionId +
                ", Type: " + type +
                ", Amount: " + amount +
                ", Time: " + timestamp;
    }
}
