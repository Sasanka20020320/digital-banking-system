package model;

import java.io.Serializable;

public class FixedDepositAccount extends Account implements Serializable {
    // Prevent Serialization errors
    private static final long serialVersionUID = 1L;

    private double interestRate;
    private int durationMonths;

    public FixedDepositAccount(int accountNumber, double balance, double interestRate, int durationMonths) {
        super(accountNumber, balance);
        this.interestRate = interestRate;
        this.durationMonths = durationMonths;
    }

    // Getters
    public double getInterestRate() {
        return interestRate;
    }

    public long getDuration() {
        return durationMonths;
    }

    public void addInterest() {
        double interest = getBalance() * interestRate;
        deposit(interest);
    }

    @Override
    public void withdraw(double amount) {
        throw new UnsupportedOperationException("Withdrawals not allowed for Fixed Deposit Account");
    }
}
