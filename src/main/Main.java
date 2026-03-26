package main;

import exception.InsufficientBalanceException;
import exception.InvalidAccountException;
import exception.InvalidAmountException;
import model.*;
import service.LoanService;
import service.TransactionService;

public class Main {
    public static void main(String[] args) {
        Customer customer = new Customer(1, "Sasanka", "sasanka@email.com", "1234");

        Account acc1 = new SavingsAccount(101, 5000, 0.1);
        Account acc2 = new StudentAccount(102, 3000);
        Account acc3 = new CheckingAccount(103, 1000, -1000);
        Account acc4 = new FixedDepositAccount(104, 10000, 0.2, 12);

        // Create Loan
        Loan loan1 = new Loan(1, 50000, customer.getUserId());

        customer.addAccount(acc1);
        customer.addAccount(acc2);
        customer.addAccount(acc3);
        customer.addAccount(acc4);

        customer.applyLoan(loan1);

        LoanService loanService = new LoanService();
        loanService.approveLoan(loan1, acc1);

        TransactionService ts = new TransactionService();

        try {
            acc1.deposit(2000);
            acc2.withdraw(2000);
            acc3.withdraw(2000);
//            acc4.withdraw(1000);
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
        System.out.println("Account 3 Balance: " + acc3.getBalance());
        System.out.println("Account 4 Balance: " + acc4.getBalance());

        System.out.println("Loan Status: " + loan1.getStatus());

        System.out.println("\nAccount 1 Transactions: ");
        for (Transaction t : acc1.getTransactions()) {
            System.out.println(t);
        }

        System.out.println("\nAccount 2 Transactions: ");
        for (Transaction t : acc2.getTransactions()) {
            System.out.println(t);
        }

        System.out.println("\nAccount 3 Transactions: ");
        for (Transaction t : acc3.getTransactions()) {
            System.out.println(t);
        }

        System.out.println("\nAccount 4 Transactions: ");
        for (Transaction t : acc4.getTransactions()) {
            System.out.println(t);
        }

        System.out.println("\nCustomer Accounts: ");
        for (Account acc : customer.getAccounts()) {
            System.out.println("Account No: " + acc.getAccountNumber());
        }
    }
    
}