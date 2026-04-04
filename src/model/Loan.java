package model;

import java.io.Serializable;

public class Loan implements Serializable {
    // Prevent Serialization errors
    private static final long serialVersionUID = 1L;

    private int loanId;
    private double amount;
    private LoanStatus status;
    private int customerId;

    private double interestRate;
    private int durationMonths;
    private double remainingBalance;
    private double monthlyInstallment;
    private int monthsPaid;
    private long nextDueDate;

    public Loan(int loanId, double amount, int customerId, double interestRate, int durationMonths) {
        this.loanId = loanId;
        this.amount = amount;
        this.status = LoanStatus.PENDING;
        this.customerId = customerId;

        this.interestRate = interestRate;
        this.durationMonths = durationMonths;
        this.remainingBalance = amount;

        calculateEMI();

        this.nextDueDate = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000);
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

    public double getRemainingBalance() { return remainingBalance; }

    public double getMonthlyInstallment() { return monthlyInstallment; }

    public int getMonthsPaid() { return monthsPaid; }

    public long getNextDueDate() { return nextDueDate; }

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

    public void setRemainingBalance(double remainingBalance) { this.remainingBalance = remainingBalance; }

    public void setMonthsPaid(int monthsPaid) { this.monthsPaid = monthsPaid; }

    public void setNextDueDate(long nextDueDate) { this.nextDueDate = nextDueDate; }

    // EMI calculations
    private void calculateEMI() {
        double monthlyRate = interestRate / 12;
        int n = durationMonths;

        monthlyInstallment =
                (amount * monthlyRate * Math.pow(1 + monthlyRate, n)) /
                        (Math.pow(1 + monthlyRate, n) - 1);
    }
}

/*
 DESIGN DECISIONS & OOP PRINCIPLES:

 1. ENCAPSULATION:
    - Loan data is private and accessed via getters/setters.

 2. ABSTRACTION:
    - EMI calculation is hidden inside calculateEMI().

 3. SINGLE RESPONSIBILITY:
    - Handles only loan-related data and calculations.

 4. STATE MANAGEMENT:
    - LoanStatus enum represents lifecycle (PENDING, APPROVED, etc.).

 5. REAL-WORLD MODELING:
    - Includes interest, duration, installments, and due dates.

 6. EXTENSIBILITY:
    - Can be extended to support penalties, early repayment, etc.
*/
