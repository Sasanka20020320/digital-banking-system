package model;

import exception.InsufficientBalanceException;

import java.io.Serializable;

public class FixedDepositAccount extends Account implements Serializable {
    // Prevent Serialization errors
    private static final long serialVersionUID = 1L;

    private double interestRate;
    private int durationMonths;
    private long startDate;

    public FixedDepositAccount(int accountNumber, double balance, double interestRate, int durationMonths) {
        super(accountNumber, balance);
        this.interestRate = interestRate;
        this.durationMonths = durationMonths;
        this.startDate = System.currentTimeMillis();
    }

    // Getters
    public double getInterestRate() {
        return interestRate;
    }

    public long getDuration() {
        return durationMonths;
    }

    // Check if the FD is matured
    public boolean isMatured() {
        long now = System.currentTimeMillis();
        long maturityTime = startDate + (durationMonths * 30L * 24 * 60 * 60 * 1000);
        return now >= maturityTime;
    }

    // Add interest if matured
    public void applyMaturityInterest() {
        if (isMatured()) {
            double interest = getBalance() * interestRate;
            deposit(interest);
            System.out.println("FD matured. Interest added.");
        }
    }

    @Override
    public void withdraw(double amount) {
        long now = System.currentTimeMillis();
        long maturityTime = startDate + (durationMonths * 30L * 24 * 60 * 60* 1000);

        if (now < maturityTime) {
            double penalty = amount * 0.10; // 10% penalty
            amount += penalty;
            System.out.println("Early withdrawal penalty: " + penalty);
        }

        if (amount > getBalance()) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        decreaseBalance(amount);
        System.out.println("Withdraw successful");
    }
}
