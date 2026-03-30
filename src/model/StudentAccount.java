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
