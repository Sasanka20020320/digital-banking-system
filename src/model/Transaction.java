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

    private boolean suspicious;

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

    public boolean isSuspicious() {
        return suspicious;
    }

    public Integer getFromAccount() {
        return fromAccount;
    }

    public Integer getToAccount() {
        return toAccount;
    }

    // Description for GUI
    public String getDescription() {
        if (type == TransactionType.TRANSFER) {
            return "From: " + fromAccount + " → To: " + toAccount;
        } else if (type == TransactionType.DEPOSIT) {
            return "Deposit of " + amount;
        } else if (type == TransactionType.WITHDRAW) {
            return "Withdrawal of " + amount;
        } else if (type == TransactionType.BILL_PAYMENT) {
            return "Bill Payment of " + amount;
        }
        return "Transaction of " + amount;
    }

    // Setter
    public void setSuspicious(boolean suspicious) {
        this.suspicious = suspicious;
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

/*
 DESIGN DECISIONS & OOP PRINCIPLES:

 1. ENCAPSULATION:
    - Transaction details are private and accessed via getters.

 2. IMMUTABILITY (PARTIAL):
    - Once created, most transaction data does not change.

 3. METHOD OVERLOADING:
    - Two constructors:
        - Simple transactions (deposit, withdraw)
        - Transfer transactions (with from/to accounts)

 4. SINGLE RESPONSIBILITY:
    - Represents a single financial transaction only.

 5. EXTENSIBILITY:
    - Supports fraud detection via 'suspicious' flag.

 6. REAL-WORLD MODELING:
    - Includes timestamp and type for accurate tracking.
*/
