package model;

import java.io.Serializable;

import exception.InsufficientBalanceException;
import exception.InvalidAmountException;

public class CheckingAccount extends Account implements Serializable {
    // Prevent Serialization errors
    private static final long serialVersionUID = 1L;

    private double overdraftLimit;

    public CheckingAccount(int accountNumber, double balance, double overdraftLimit) {
        super(accountNumber, balance);
        this.overdraftLimit = overdraftLimit;
    }

    // Getter
    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    // Overriding basic withdraw method for Overdrafting
    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdraw amount must be positive");
        }

        if (getBalance() - amount < overdraftLimit) {
            throw new InsufficientBalanceException("Balance after withdraw must not go below overdraftLimit");
        }

        // No minimum balance check
        double newBalance = getBalance() - amount;

        // Update balance
        setBalance(newBalance);

        addTransaction(new Transaction(amount, TransactionType.WITHDRAW));
    }
}

/*
 DESIGN DECISIONS & OOP PRINCIPLES:

 1. INHERITANCE:
    - Extends Account.

 2. POLYMORPHISM:
    - Overrides withdraw() to support overdraft behavior.

 3. ENCAPSULATION:
    - Overdraft limit is controlled internally.

 4. REAL-WORLD MODELING:
    - Allows negative balance within overdraft limit.
*/
