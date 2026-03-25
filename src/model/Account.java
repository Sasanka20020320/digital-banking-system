package model;

import exception.InsufficientBalanceException;
import exception.InvalidAmountException;

import java.util.ArrayList;
import java.util.List;

public class Account {
    // Parent attributes
    private int accountNumber;
    private double balance;
    protected double minimumBalance = 1000;

    // ArrayList to store transactions
    private List<Transaction> transactions;

    // Parent constructor for all account types
    public Account(int accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;

        // Initialize Transactions list
        transactions = new ArrayList<>();
    }

    // Getters for private attributes
    public int getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    // Setters for attributes
    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    // Transaction method
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    // Deposit method
    public void deposit(double amount) {
        if(amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive");
        }
        balance = balance + amount;

        // Store the transaction
        Transaction transaction = new Transaction(amount, TransactionType.DEPOSIT);
        addTransaction(transaction);
    }

    // Withdraw method
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdraw amount must be positive");
        }

        if (balance - amount < minimumBalance) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
        balance = balance - amount;

        // Store the transaction
        Transaction transaction = new Transaction(amount, TransactionType.WITHDRAW);
        addTransaction(transaction);
    }
}
