package controller;

import java.util.List;
import java.util.Scanner;
import model.*;
import service.*;

public class BankController {
    private AuthService authService;
    private TransactionService transactionService;
    private LoanService loanService;

    public BankController(AuthService authService, TransactionService transactionService, LoanService loanService) {
        this.authService = authService;
        this.transactionService = transactionService;
        this.loanService = loanService;
    }

    // Find an account
    private Account findAccount(Customer customer, int accNo) {
        for (Account acc : customer.getAccounts()) {
            if (acc.getAccountNumber() == accNo) {
                return acc;
            }
        }
        return null;
    }

    // Generate a Loan Id
    private int generateLoanId(Customer customer) {
        return customer.getLoans().size() + 1;
    }

    public void login(List<User> users, Scanner scanner) {

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

        Customer customer = null;

        for (User user : users) {
            if (user instanceof Customer) {
                customer = (Customer) user;
                break;
            }
        }

        if (loggedInUser instanceof Customer) {
            customerMenu((Customer) loggedInUser, scanner);
        } else if (loggedInUser instanceof Staff) {
            staffMenu((Staff) loggedInUser, customer, scanner);
        }

//        System.out.println("Welcome, " + customer.getName());
    }

    // Customer menu
    public void customerMenu(Customer customer, Scanner scanner) {
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

                    deposit(customer, depAccNo, depAmount);
                    break;

                // Withdraw
                case 2:
                    System.out.println("Enter account number: ");
                    int withAccNo = scanner.nextInt();

                    System.out.println("Enter amount: ");
                    double withAmount = scanner.nextDouble();

                    withdraw(customer, withAccNo, withAmount);
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

                    transfer(customer, fromAccNo, toAccNo, amount);
                    break;

                // Apply Loan
                case 4:
                    System.out.println("Enter loan amount: ");
                    double loanAmount = scanner.nextDouble();

                    applyLoan(customer, loanAmount);
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

    // Staff menu
    public void staffMenu(Staff staff, Customer customer, Scanner scanner) {
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
                    staff.approveLoan(pendingLoan, customer.getAccounts().get(0), customer, loanService);

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

    // Action methods
    // Deposit
    public void deposit(Customer customer, int accNo, double amount) {
        Account acc = findAccount(customer, accNo);
        if (acc != null) {
            transactionService.deposit(acc, amount, customer);
        } else {
            System.out.println("Account not found");
        }
    }

    // Withdraw
    public void withdraw(Customer customer, int accNo, double amount) {
        Account acc = findAccount(customer, accNo);
        if (acc != null) {
            transactionService.withdraw(acc, amount, customer);
        } else {
            System.out.println("Account not found");
        }
    }

    // Transfer
    public void transfer(Customer customer, int accFrom, int accTo, double amount) {
        Account from = findAccount(customer, accFrom);
        Account to = findAccount(customer, accTo);
        if (from != null && to != null) {
            transactionService.transfer(from, to, amount, customer);
        } else {
            System.out.println("Account(s) not found");
        }
    }

    // Apply Loan
    public void applyLoan(Customer customer, double amount) {
        if (amount <= 0) {
            System.out.println("Invalid loan amount");
            return;
        }

        Loan loan = new Loan(generateLoanId(customer), amount, customer.getUserId());
        customer.applyLoan(loan);

        customer.notifyUser("Loan request submitted: " + amount);

        System.out.println("Loan request submitted. Waiting for approval...");
    }
}
