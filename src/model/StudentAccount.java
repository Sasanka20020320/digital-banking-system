package model;

public class StudentAccount extends Account {
    public StudentAccount(int accountNumber, double balance) {
        super(accountNumber, balance);
        this.minimumBalance = 500;
    }
}
