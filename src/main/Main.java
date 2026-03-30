package main;

import java.util.Scanner;
import exception.InsufficientBalanceException;
import exception.InvalidAccountException;
import exception.InvalidAmountException;
import model.*;
import service.LoanService;
import service.TransactionService;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Customer customer = new Customer(1, "Sasanka", "sasanka@email.com", "1234");

        Account acc1 = new SavingsAccount(101, 5000, 0.1);
        Account acc2 = new StudentAccount(102, 3000);
        Account acc3 = new CheckingAccount(103, 1000, -1000);
        Account acc4 = new FixedDepositAccount(104, 10000, 0.2, 12);

        customer.addAccount(acc1);
        customer.addAccount(acc2);
        customer.addAccount(acc3);
        customer.addAccount(acc4);

        TransactionService ts = new TransactionService();

        LoanService loanService = new LoanService();

        System.out.println("Welcome, " + customer.getName());

        boolean running = true;

        while (running) {
            System.out.println("\n=== BANKING SYSTEM MENU ===");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer");
            System.out.println("4. Apply Loan");
            System.out.println("5. View Notifications");
            System.out.println("6. Check Balance");
            System.out.println("7. Exit");
            System.out.println("Choose option: ");

            int choice = scanner.nextInt();

            switch (choice) {
                // Deposit
                case 1:
                    System.out.println("Enter account number: ");
                    int depAccNo = scanner.nextInt();

                    System.out.println("Enter amount: ");
                    double depAmount = scanner.nextDouble();

                    Account depAcc = null;
                    for (Account acc : customer.getAccounts()) {
                        if (acc.getAccountNumber() == depAccNo) {
                            depAcc = acc;
                            break;
                        }
                    }

                    if (depAcc != null) {
                        ts.deposit(depAcc, depAmount, customer);
                    } else {
                        System.out.println("Account not found");
                    }
                    break;

                // Withdraw
                case 2:
                    System.out.println("Enter account number: ");
                    int withAccNo = scanner.nextInt();

                    System.out.println("Enter amount: ");
                    double withAmount = scanner.nextDouble();

                    Account withAcc = null;
                    for (Account acc : customer.getAccounts()) {
                        if (acc.getAccountNumber() == withAccNo) {
                            withAcc = acc;
                            break;
                        }
                    }

                    if (withAcc != null) {
                        ts.withdraw(withAcc, withAmount, customer);
                    } else {
                        System.out.println("Account not found");
                    }
                    break;

                    // Transfer
                case 3:
                    System.out.println("Your Accounts: ");
                    for (Account acc : customer.getAccounts()) {
                        System.out.println("Account No: " + acc.getAccountNumber());
                    }

                    System.out.println("From Account: ");
                    int fromAccNo = scanner.nextInt();

                    System.out.println("To Account: ");
                    int toAccNo = scanner.nextInt();

                    System.out.println("Amount: ");
                    double amount = scanner.nextDouble();

                    Account fromAcc = null;
                    Account toAcc = null;

                    for (Account acc : customer.getAccounts()) {
                        if (acc.getAccountNumber() == fromAccNo) fromAcc = acc;
                        if (acc.getAccountNumber() == toAccNo) toAcc = acc;
                    }

                    if (fromAcc != null && toAcc != null) {
                        ts.transfer(fromAcc, toAcc, amount, customer);
                    } else {
                        System.out.println("Invalid account(s)");
                    }
                    break;

                    // Apply Loan
                case 4:
                    System.out.println("Enter loan amount: ");
                    double loanAmount = scanner.nextDouble();

                    Loan loan = new Loan(2, loanAmount, customer.getUserId());
                    customer.applyLoan(loan);

                    loanService.approveLoan(loan, acc1, customer);

                    System.out.println("Loan applied and processed");
                    break;

                // View Notifications
                case 5:
                    System.out.println("\nNotifications: ");
                    for (Notification n : customer.getNotifications()) {
                        System.out.println(n);
                    }
                    break;

                // Check Balance
                case 6:
                    System.out.println("Your Accounts: ");
                    for (Account acc : customer.getAccounts()) {
                        System.out.println("Account No: " + acc.getAccountNumber());
                    }

                    System.out.println("Enter account number: ");
                    int balAccNo = scanner.nextInt();

                    Account balAcc = null;

                    for (Account acc : customer.getAccounts()) {
                        if (acc.getAccountNumber() == balAccNo) {
                            balAcc = acc;
                            break;
                        }
                    }

                    if (balAcc != null) {
                        System.out.println("Balance: " + balAcc.getBalance());
                    } else {
                        System.out.println("Account not found");
                    }
                    break;

                // Exit
                case 7:
                    running = false;
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid choice");
            }
//
//
//        // Create Loan
//        Loan loan1 = new Loan(1, 50000, customer.getUserId());
//
//
//        customer.applyLoan(loan1);
//
//        loanService.approveLoan(loan1, acc1, customer);
//
//
//        try {
//            ts.deposit(acc1, 2000, customer);
//            ts.withdraw(acc2, 2000, customer);
//            ts.withdraw(acc3,2000, customer);
////            acc4.withdraw(1000);
//            ts.transfer(acc1, acc2, 1500, customer);
//        } catch (InvalidAmountException e) {
//            System.out.println("Amount Error: " + e.getMessage());
//        } catch (InsufficientBalanceException e) {
//            System.out.println("Balance Error: " + e.getMessage());
//        } catch (InvalidAccountException e) {
//            System.out.println("Account Error: " + e.getMessage());
//        }
//
//        System.out.println("Account 1 Balance: " + acc1.getBalance());
//        System.out.println("Account 2 Balance: " + acc2.getBalance());
//        System.out.println("Account 3 Balance: " + acc3.getBalance());
//        System.out.println("Account 4 Balance: " + acc4.getBalance());
//
//        System.out.println("Loan Status: " + loan1.getStatus());
//
//        System.out.println("\nAccount 1 Transactions: ");
//        for (Transaction t : acc1.getTransactions()) {
//            System.out.println(t);
//        }
//
//        System.out.println("\nAccount 2 Transactions: ");
//        for (Transaction t : acc2.getTransactions()) {
//            System.out.println(t);
//        }
//
//        System.out.println("\nAccount 3 Transactions: ");
//        for (Transaction t : acc3.getTransactions()) {
//            System.out.println(t);
//        }
//
//        System.out.println("\nAccount 4 Transactions: ");
//        for (Transaction t : acc4.getTransactions()) {
//            System.out.println(t);
//        }
//
//        System.out.println("\nCustomer Accounts: ");
//        for (Account acc : customer.getAccounts()) {
//            System.out.println("Account No: " + acc.getAccountNumber());
//        }
//
//        System.out.println("\nNotifications:");
//        for (Notification n : customer.getNotifications()) {
//            System.out.println(n);
        }
    }
    
}