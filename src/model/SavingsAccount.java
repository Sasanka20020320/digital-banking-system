package model;

import java.io.Serializable;
import java.util.List;

public class SavingsAccount extends Account implements Serializable {
    // Prevent Serialization errors
    private static final long serialVersionUID = 1L;

    private double interestRate;

    public SavingsAccount(int accountNumber, double balance, double interestRate) {
        super(accountNumber, balance);
        this.interestRate = interestRate;
    }

    // Getter
    public double getInterestRate() {
        return interestRate;
    }

    public void addInterest() {
        double interest = getBalance() * interestRate;

        if (interest > 0) {
            setBalance(getBalance() + interest);
        }
    }
}

/*
 DESIGN DECISIONS & OOP PRINCIPLES:

 1. INHERITANCE:
    - Extends Account and reuses core functionality.

 2. SPECIALIZATION:
    - Adds interestRate and interest calculation.

 3. POLYMORPHISM:
    - Can override behaviors if needed.

 4. SINGLE RESPONSIBILITY:
    - Focused only on savings account behavior.
*/
