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

    private Integer fromAccount;
    private Integer toAccount;

    /*
    Constructor Overloading
        - 2-arg constructor --> simple transactions
        - 4-arg constructor --> transfer only
     */

    // For normal transactions (deposit, withdraw, bill)
    public Transaction(double amount, TransactionType type) {
        this.transactionId = ++counter;
        this.amount = amount;
        this.type = type;
        this.timestamp = Instant.now();
    }

    // For transfer transactions
    public Transaction(double amount, TransactionType type, Integer fromAccount, Integer toAccount) {
        this.transactionId = ++counter; // Auto-increment ID
        this.amount = amount;
        this.type = type;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
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
                ", From: " + (fromAccount != null ? fromAccount : "-") +
                ", To: " + (toAccount != null ? toAccount : "-") +
                ", Time: " + timestamp;
    }
}
