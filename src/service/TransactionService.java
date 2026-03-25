package service;

import exception.InvalidAccountException;
import exception.InvalidAmountException;
import model.Account;

public class TransactionService {
    public void transfer(Account from, Account to, double amount) {
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

        System.out.println("Transfer successful");
    }
}
