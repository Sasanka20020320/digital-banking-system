package service;

import model.Account;

public class TransactionService {
    public boolean transfer(Account from, Account to, double amount) {
        // Validate sender and receiver accounts
        if (from == null || to == null) {
            System.out.println("Invalid account");
            return false;
        }

        // Prevent from self-transfer
        if (from == to) {
            System.out.println("Cannot transfer to the same account");
            return false;
        }

        // Validate the amount
        if (amount <= 0) {
            System.out.println("Invalid amount");
            return false;
        }

        // Store the initial balance for checking the transfer is successful or not.
        double initialBalance = from.getBalance();

        // First Step: Withdraw from the sender's account
        from.withdraw(amount);

        // Check if the transaction is successful or not
        if (from.getBalance() == initialBalance) {
            System.out.println("Transfer failed: Insufficient balance");
            return false;
        }

        // Second Step: Deposit into the receiver's account
        to.deposit(amount);

        System.out.println("Transfer successful");
        return true;
    }
}
