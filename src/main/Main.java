package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import exception.InsufficientBalanceException;
import exception.InvalidAccountException;
import exception.InvalidAmountException;
import model.*;
import service.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        List<User> users = new ArrayList<>();

        Customer customer = new Customer(1, "Sasanka", "sasanka@email.com", "1234");
        Staff staff = new Staff(2, "Wishwa", "wishwa@email.com", "1234");

        users.add(customer);
        users.add(staff);

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

        AuthService authService = new AuthService();
        System.out.println("=== Login ===");
        System.out.println("Email: ");
        String email = scanner.next();

        System.out.println("Password: ");
        String password = scanner.next();

        User loggedInUser = authService.login(email, password, users);

        if (loggedInUser == null) {
            System.out.println("Invalid credentials");
            return;
        }

        System.out.println("Welcome, " + customer.getName());

        if (loggedInUser instanceof Customer) {
            customer = (Customer) loggedInUser;

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

                        System.out.println("Loan request submitted. Waiting for approval...");
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
            }
        }
        else if (loggedInUser instanceof Staff) {
            staff = (Staff) loggedInUser;

            boolean running = true;

            while (running) {
                System.out.println("\n=== STAFF MENU ===");
                System.out.println("1. Approve Loan");
                System.out.println("2. Exit");
                System.out.println("Choose option: ");

                int choice = scanner.nextInt();

                switch (choice) {
                    // Approve Loan
                    case 1:
                        if (customer.getLoans().isEmpty()) {
                            System.out.println("No loan requests");
                            break;
                        }

                        Loan pendingLoan = customer.getLoans().get(0);
                        staff.approveLoan(pendingLoan, acc1, customer, loanService);

                        System.out.println("Loan approved by staff");
                        break;

                    // Exit
                    case 2:
                        running = false;
                        System.out.println("Exiting...");
                        break;

                    default:
                        System.out.println("Invalid choice");
                }
            }
        }
    }
}