package model;

import java.io.Serializable;

public class Loan implements Serializable {
    // Prevent Serialization errors
    private static final long serialVersionUID = 1L;

    private int loanId;
    private double amount;
    private LoanStatus status;
    private int customerId;

    public Loan(int loanId, double amount, int customerId) {
        this.loanId = loanId;
        this.amount = amount;
        this.status = LoanStatus.PENDING;
        this.customerId = customerId;
    }

    // Getters
    public int getLoanId() {
        return loanId;
    }

    public double getAmount() {
        return amount;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public int getCustomerId() {
        return customerId;
    }

    // Setters
    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}
