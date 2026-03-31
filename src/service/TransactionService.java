package service;

import exception.InsufficientBalanceException;
import exception.InvalidAccountException;
import exception.InvalidAmountException;
import model.Account;
import model.Customer;
import model.Transaction;
import model.TransactionType;

public class TransactionService {
    public void transfer(Account from, Account to, double amount, Customer customer) {
        // Validate sender and receiver accounts
        if (from == null || to == null) {
            throw new InvalidAccountException("Invalid account");
        }

        // Prevent from self-transfer
        if (from == to) {
            throw new InvalidAccountException("Cannot transfer to same account");
        }

        // Validate the amount
        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be positive");
        }

        // Balance validation
        if (!from.canWithdraw(amount)) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        // First Step: decrease balance from the sender's account
        from.decreaseBalance(amount);

        // Second Step: deposit into the receiver's account
        to.increaseBalance(amount);

        // Record as transfer transactions
        Transaction t = new Transaction(amount, TransactionType.TRANSFER, from.getAccountNumber(), to.getAccountNumber());
        from.addTransaction(t);
        to.addTransaction(t);

        customer.notifyUser("Transfer of " + amount + " completed successfully");

        System.out.println("Transfer successful");
    }

    // Notify Customer about Deposits
    public void deposit(Account account, double amount, Customer customer) {
        account.deposit(amount);
        customer.notifyUser("Deposited " + amount + " to account " + account.getAccountNumber());
    }

    // Notify Customer about Withdrawals
    public void withdraw(Account account, double amount, Customer customer) {
        account.withdraw(amount);
        customer.notifyUser("Withdrawn " + amount + " from account " + account.getAccountNumber());
    }
}
