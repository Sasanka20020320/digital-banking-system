package service;

import exception.InvalidAccountException;
import exception.InvalidAmountException;
import model.Account;
import model.Customer;

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

        // First Step: Withdraw from the sender's account
        from.withdraw(amount);

        // Second Step: Deposit into the receiver's account
        to.deposit(amount);

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
