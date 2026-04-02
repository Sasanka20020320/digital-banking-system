package service;

import exception.InsufficientBalanceException;
import exception.InvalidAccountException;
import exception.InvalidAmountException;
import model.*;

public class TransactionService {
    public void transfer(Account from, Account to, double amount, Customer customer) {
        try {
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
            from.withdraw(amount, TransactionType.TRANSFER);

            // Second Step: deposit into the receiver's account
            to.increaseBalance(amount);

            // Record as transfer transactions
            Transaction t = new Transaction(amount, TransactionType.TRANSFER, from.getAccountNumber(), to.getAccountNumber());
            to.addTransaction(t);

            checkLowBalance(from, customer);
            fraudService.analyzeTransaction(to, t);

            customer.notifyUser("Transfer of " + amount + " completed successfully", Notification.NotificationType.INFO);
            System.out.println("Transfer successful");
        } catch (Exception e) {
            customer.notifyUser("Transfer failed: " + e.getMessage(), Notification.NotificationType.ALERT);
        }
    }

    // Low Balance alert
    private void checkLowBalance(Account account, Customer customer) {
        if (account.getBalance() < 2000) {
            customer.notifyUser("Warning: Low balance in account " + account.getAccountNumber(), Notification.NotificationType.WARNING);
        }
    }

    // Notify Customer about Deposits
    public void deposit(Account account, double amount, Customer customer) {
        account.deposit(amount);
        customer.notifyUser("Deposited " + amount + " to account " + account.getAccountNumber(), Notification.NotificationType.INFO);

        checkLowBalance(account, customer);

        // Get last transaction
        Transaction transaction = account.getTransactions()
                        .get(account.getTransactions().size() - 1);

        fraudService.analyzeTransaction(account, transaction);
    }

    // Notify Customer about Withdrawals
    public void withdraw(Account account, double amount, Customer customer) {
        account.withdraw(amount);
        customer.notifyUser("Withdrawn " + amount + " from account " + account.getAccountNumber(), Notification.NotificationType.INFO);

        checkLowBalance(account, customer);

        // Get last transaction
        Transaction transaction = account.getTransactions()
                .get(account.getTransactions().size() - 1);

        fraudService.analyzeTransaction(account, transaction);
    }

    private FraudDetectionService fraudService = new FraudDetectionService();
}
