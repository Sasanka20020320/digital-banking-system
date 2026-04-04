package model;

import java.io.Serializable;

public class StudentAccount extends Account implements Serializable {
    // Prevent Serialization errors
    private static final long serialVersionUID = 1L;

    public StudentAccount(int accountNumber, double balance) {
        super(accountNumber, balance);
        this.minimumBalance = 500;
    }
}

/*
 DESIGN DECISIONS & OOP PRINCIPLES:

 1. INHERITANCE:
    - Extends Account.

 2. SPECIALIZATION:
    - Reduces minimum balance requirement.

 3. REUSABILITY:
    - Uses parent functionality without modification.
*/
