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

    public BillPayment(String billType, double amount) {
        this.billId = ++counter;
        this.billType = billType;
        this.amount = amount;
        this.timestamp = Instant.now();
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

    @Override
    public String toString() {
        return "Bill ID: " + billId +
                ", Type: " + billType +
                ", Amount: " + amount +
                ", Time: " + timestamp;
    }
}