package model;

import java.time.Instant;
import java.io.Serializable;

public class Transaction implements Serializable {
    // Prevent Serialization errors
    private static final long serialVersionUID = 1L;

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
        this.transactionId = generateId();
        this.amount = amount;
        this.type = type;
        this.timestamp = Instant.now();
    }

    // For transfer transactions
    public Transaction(double amount, TransactionType type, Integer fromAccount, Integer toAccount) {
        this.transactionId = generateId(); // Auto-increment ID
        this.amount = amount;
        this.type = type;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.timestamp = Instant.now();
    }

    // Generate Transaction ID
    private int generateId() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
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
        String base = "ID: " + transactionId +
                ", Type: " + type +
                ", Amount: " + amount;

        if (type == TransactionType.TRANSFER) {
            base += ", From: " + fromAccount +
                    ", To: " + toAccount;
        }

        base += ", Time: " + timestamp;

        return base;
    }
}
