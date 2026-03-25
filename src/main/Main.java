package main;

import exception.InsufficientBalanceException;
import exception.InvalidAccountException;
import exception.InvalidAmountException;
import model.*;
import service.TransactionService;

public class Main {
    public static void main(String[] args) {
        Customer customer = new Customer(1, "Sasanka", "sasanka@email.com", "1234");

        Account acc1 = new Account(101, 5000);
        Account acc2 = new Account(102, 3000);

        customer.addAccount(acc1);
        customer.addAccount(acc2);

        TransactionService ts = new TransactionService();

        try {
            acc1.deposit(2000);
            acc2.withdraw(2000);
            ts.transfer(acc1, acc2, 1500);
        } catch (InvalidAmountException e) {
            System.out.println("Amount Error: " + e.getMessage());
        } catch (InsufficientBalanceException e) {
            System.out.println("Balance Error: " + e.getMessage());
        } catch (InvalidAccountException e) {
            System.out.println("Account Error: " + e.getMessage());
        }

        System.out.println("Account 1 Balance: " + acc1.getBalance());
        System.out.println("Account 2 Balance: " + acc2.getBalance());

        System.out.println("\nAccount 1 Transactions: ");
        for (Transaction t : acc1.getTransactions()) {
            System.out.println(t);
        }

        System.out.println("\nAccount 2 Transactions: ");
        for (Transaction t : acc2.getTransactions()) {
            System.out.println(t);
        }

        System.out.println("\nCustomer Accounts: ");
        for (Account acc : customer.getAccounts()) {
            System.out.println("Account No: " + acc.getAccountNumber());
        }
    }
    
}