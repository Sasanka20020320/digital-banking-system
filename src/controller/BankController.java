package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.*;
import service.*;

public class BankController {
    private AuthService authService;
    private TransactionService transactionService;
    private LoanService loanService;
    private BillPaymentService billService;

    public BankController(AuthService authService, TransactionService transactionService, LoanService loanService, BillPaymentService billService) {
        this.authService = authService;
        this.transactionService = transactionService;
        this.loanService = loanService;
        this.billService = billService;
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

    // Find an account of another customer
    private Account findAccountGlobal(List<User> users, int accNo) {
        for (User user : users) {
            if (user instanceof Customer) {
                Customer customer = (Customer) user;

                for (Account acc : customer.getAccounts()) {
                    if (acc.getAccountNumber() == accNo) {
                        return acc;
                    }
                }
            }
        }
        return null;
    }

    // Generate a Loan Id
    private int generateLoanId(Customer customer) {
        return customer.getLoans().size() + 1;
    }

    // Generate Account Number
    private int generateAccountNumber(List<User> users) {
        int maxAccNo = 100;

        for (User user : users) {
            if (user instanceof Customer) {
                Customer c = (Customer) user;

                for (Account acc : c.getAccounts()) {
                    if (acc.getAccountNumber() > maxAccNo) {
                        maxAccNo = acc.getAccountNumber();
                    }
                }
            }
        }
        return maxAccNo + 1;
    }

    public void register(List<User> users, Scanner scanner) {
        System.out.println("=== CUSTOMER REGISTRATION ===");
        scanner.nextLine();

        String name, email, password;

        System.out.println("Enter Name: ");
        name = scanner.nextLine();

        while (true) {
            System.out.println("Enter Email: ");
            email = scanner.nextLine();

            String finalEmail = email;
            boolean exists = users.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(finalEmail));

            if (exists) {
                System.out.println("Email already exists. Please use a different email.");
            } else {
                break;
            }
        }

        System.out.println("Enter Password: ");
        password = scanner.nextLine();

        // Generate new ID
        int newId = users.stream().mapToInt(User::getUserId).max().orElse(0) + 1;

        Customer newCustomer = new Customer(newId, name, email, password);

        // Create a default account
        int newAccNo = generateAccountNumber(users);
        System.out.println("Enter initial deposit amount: ");
        double initBalance = scanner.nextDouble();
        scanner.nextLine();

        Account acc = new SavingsAccount(newAccNo, initBalance, 0.05);
        newCustomer.addAccount(acc);

        System.out.println("Savings account created with Account No: " + newAccNo);

        // Ask if user wants to create another account
        boolean addMore = true;

        while (addMore) {
            System.out.println("Do you want to open an additional account? (yes/no): ");
            String choice = scanner.nextLine().trim().toLowerCase();

            if (choice.equals("yes")) {
                openNewAccount(newCustomer, users, scanner);
            } else {
                addMore = false;
            }
        }

        users.add(newCustomer);
        System.out.println("Registration successful! You can now login.");
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
            customerMenu((Customer) loggedInUser, users, scanner);
        } else if (loggedInUser instanceof Staff) {
            staffMenu((Staff) loggedInUser, users, scanner);
        } else if (loggedInUser instanceof Admin) {
            adminMenu((Admin) loggedInUser, users, scanner);
        }

//        System.out.println("Welcome, " + customer.getName());
    }

    // Customer menu
    public void customerMenu(Customer customer, List<User> users, Scanner scanner) {
        boolean running = true;

        while (running) {
            System.out.println("\n=== BANKING SYSTEM MENU ===");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer");
            System.out.println("4. Apply Loan");
            System.out.println("5. View Notifications");
            System.out.println("6. Check Balance");
            System.out.println("7. Pay Bill");
            System.out.println("8. View Transactions");
            System.out.println("9. Open New Account");
            System.out.println("10. Exit");
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
                    System.out.println("Your Accounts: ");
                    for (Account acc : customer.getAccounts()) {
                        System.out.println("Account No: " + acc.getAccountNumber());
                    }

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

                    transfer(customer, users, fromAccNo, toAccNo, amount);
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

                // Bill payment
                case 7:
                    System.out.println("Your Accounts: ");
                    for (Account acc : customer.getAccounts()) {
                        System.out.println("Account No: " + acc.getAccountNumber());
                    }

                    System.out.println("Enter account number: ");
                    int billAccNo = scanner.nextInt();

                    System.out.println("Enter bill type (electricity/water/internet): ");
                    String billType = scanner.next();

                    System.out.println("Enter amount: ");
                    double billAmount = scanner.nextDouble();

                    payBill(customer, billAccNo, billType, billAmount);
                    break;

                // View Transactions
                case 8:
                    System.out.println("Enter account number: ");
                    int transAccNo = scanner.nextInt();

                    viewTransactions(customer, transAccNo, scanner);
                    break;

                // Open New Account
                case 9:
                    openNewAccount(customer, users, scanner);
                    break;

                // Exit
                case 10:
                    running = false;
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    // Staff menu
    public void staffMenu(Staff staff, List<User> users, Scanner scanner) {
        boolean running = true;

        while (running) {
            System.out.println("\n=== STAFF MENU ===");
            System.out.println("1. Approve Loan");
            System.out.println("2. Reject Loan");
            System.out.println("3. Exit");
            System.out.println("Choose option: ");

            int choice = scanner.nextInt();

            switch (choice) {
                // Collect all loans from all customer
                case 1: // Approve Loan
                case 2: // Reject Loan

                    List<Loan> allLoans = new ArrayList<>();
                    List<Customer> loanCustomers = new ArrayList<>();

                    // Collect all loans from all customers
                    for (User user : users) {
                        if (user instanceof Customer) {
                            Customer c = (Customer) user;

                            for (Loan loan : c.getLoans()) {
                                allLoans.add(loan);
                                loanCustomers.add(c);
                            }
                        }
                    }

                    if (allLoans.isEmpty()) {
                        System.out.println("No loan requests available");
                        break;
                    }

                    // Display loans
                    System.out.println("\n=== LOAN LIST ===");
                    for (int i = 0; i < allLoans.size(); i++) {
                        Loan loan = allLoans.get(i);
                        System.out.println((i + 1) + ". Loan ID: " + loan.getLoanId() +
                                ", Amount: " + loan.getAmount() +
                                ", Status: " + loan.getStatus() +
                                ", Customer ID: " + loan.getCustomerId());
                    }

                    System.out.println("Select loan number: ");
                    int index = scanner.nextInt() - 1;

                    if (index < 0 || index >= allLoans.size()) {
                        System.out.println("Invalid selection");
                        break;
                    }

                    Loan selectedLoan = allLoans.get(index);
                    Customer loanOwner = loanCustomers.get(index);

                    if (selectedLoan.getStatus() != LoanStatus.PENDING) {
                        System.out.println("Loan already processed");
                        break;
                    }

                    // Approve or Reject
                    if (choice == 1) {
                        staff.approveLoan(selectedLoan, loanOwner.getAccounts().get(0), loanOwner, loanService);
                        System.out.println("Loan approved");
                    } else {
                        staff.rejectLoan(selectedLoan, loanService);
                        System.out.println("Loan rejected");
                    }

                    break;

                // Exit
                case 3:
                    running = false;
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    // Admin menu
    public void adminMenu(Admin admin, List<User> users, Scanner scanner) {
        boolean running = true;

        while (running) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. View All Users");
            System.out.println("2. View User Accounts");
            System.out.println("3. View All Loans");
            System.out.println("4. Loan Performance Report");
            System.out.println("5. Exit");
            System.out.println("Choose option: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    viewAllUsers(users);
                    break;
                case 2:
                    viewAllAccounts(users);
                    break;
                case 3:
                    viewAllLoans(users);
                    break;
                case 4:
                    generateLoanReport(users);
                    break;
                case 5:
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
    public void transfer(Customer customer, List<User> users, int accFrom, int accTo, double amount) {
        Account from = findAccount(customer, accFrom);
        Account to = findAccountGlobal(users, accTo);

        if (from != null && to != null) {
            transactionService.transfer(from, to, amount, customer);
            System.out.println("Transfer successful!");
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

    // Pay Bills
    public void payBill(Customer customer, int accNo, String billType, double amount) {
        Account acc = findAccount(customer, accNo);

        if (acc != null) {
            try {
                billService.payBill(acc, billType, amount,customer);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Account not found");
        }
    }

    // Open new Account
    public void openNewAccount(Customer customer, List<User> users, Scanner scanner) {
        System.out.println("=== OPEN NEW ACCOUNT ===");

        System.out.println("Select account type: ");
        System.out.println("1. Savings");
        System.out.println("2. Checking");
        System.out.println("3. Student");
        System.out.println("4. Fixed Deposit");

        int type = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter initial deposit: ");
        double balance = scanner.nextDouble();
        scanner.nextLine();

        int accNo = generateAccountNumber(users);
        Account newAcc = null;

        switch (type) {
            case 1:
                newAcc = new SavingsAccount(accNo, balance, 0.05);
                break;
            case 2:
                System.out.println("Enter overdraft limit: ");
                double overdraft = scanner.nextDouble();
                scanner.nextLine();
                newAcc = new CheckingAccount(accNo, balance, overdraft);
                break;
            case 3:
                newAcc = new StudentAccount(accNo, balance);
                break;
            case 4:
                System.out.println("Enter months for deposit: ");
                int months = scanner.nextInt();
                scanner.nextLine();
                newAcc = new FixedDepositAccount(accNo, balance, 0.2, months);
                break;
            default:
                System.out.println("invalid account type");
                return;
        }
        customer.addAccount(newAcc);
        customer.notifyUser("New account created: " + accNo);
        System.out.println("Account created successfully! Account No: " + accNo);
    }

    // View Transactions
    public void viewTransactions(Customer customer, int accNo, Scanner scanner) {
        Account acc = findAccount(customer, accNo);

        if (acc != null) {
            List<Transaction> transactions = acc.getTransactions();

            if (transactions.isEmpty()) {
                System.out.println("No transactions found.");
            } else {
                boolean running = true;
                boolean found = false;

                while (running) {
                    System.out.println("\n=== TRANSACTION HISTORY (Account " + accNo + ") ===");
                    System.out.println("1. All Transactions");
                    System.out.println("2. Deposit Transactions");
                    System.out.println("3. Withdraw Transactions");
                    System.out.println("4. Transfer Transactions");
                    System.out.println("5. Bill Payment Transactions");
                    System.out.println("6. Exit");
                    System.out.println("Choose option: ");

                    int choice = scanner.nextInt();

                    switch (choice) {
                        // All Transactions
                        case 1:
                            for (Transaction t : transactions) {
                                System.out.println(t);
                                found = true;
                            }

                            if (!found) {
                                System.out.println("No transactions found.");
                            }
                            break;

                        // Deposits
                        case 2:
                            found = false;

                            for (Transaction td : transactions) {
                                if (td.getType() == TransactionType.DEPOSIT) {
                                    System.out.println(td);
                                    found = true;
                                }
                            }

                            if (!found) {
                                System.out.println("No deposit transactions found.");
                            }
                            break;

                        // Withdrawals
                        case 3:
                            found = false;

                            for (Transaction tw : transactions) {
                                if (tw.getType() == TransactionType.WITHDRAW) {
                                    System.out.println(tw);
                                    found = true;
                                }
                            }

                            if (!found) {
                                System.out.println("No withdraw transactions found.");
                            }
                            break;

                        // Transfers
                        case 4:
                            found = false;
                            for (Transaction tt : transactions) {
                                if (tt.getType() == TransactionType.TRANSFER) {
                                    System.out.println(tt);
                                    found = true;
                                }
                            }

                            if (!found) {
                                System.out.println("No transfer transactions found.");
                            }
                            break;

                        // Bill Payments
                        case 5:
                            for (Transaction tb : transactions) {
                                if (tb.getType() == TransactionType.BILL_PAYMENT) {
                                    System.out.println(tb);
                                    found = true;
                                }
                            }

                            if (!found) {
                                System.out.println("No transactions found.");
                            }
                            break;

                        // Exit
                        case 6:
                            running = false;
                            System.out.println("Exiting...");
                            break;

                        default:
                            System.out.println("Invalid Choice");
                    }
                }
            }
        } else {
            System.out.println("Account not found");
        }
    }

    // View all users
    private void viewAllUsers(List<User> users) {
        for(User user : users) {
            System.out.println(user);
        }
    }

    // View all accounts
    private void viewAllAccounts(List<User> users) {
        boolean found = false;

        for (User user : users) {
            if (user instanceof Customer) {
                Customer customer = (Customer) user;

                for (Account acc : customer.getAccounts()) {
                    System.out.println("Account No: " + acc.getAccountNumber() +
                            ", Balance: " + acc.getBalance());
                    found = true;
                }
            }
        }
        if (!found) {
            System.out.println("No accounts found");
        }
    }

    // View all loans
    private void viewAllLoans(List<User> users) {
        for (User user : users) {
            if (user instanceof Customer) {
                Customer customer = (Customer) user;

                for (Loan loan : customer.getLoans()) {
                    System.out.println("Loan ID: " + loan.getLoanId() +
                            ", Amount: " + loan.getAmount() +
                            ", Status: " + loan.getStatus() +
                            ", Customer ID: " + loan.getCustomerId());
                }
            }
        }
    }

    // Loan Performance Reports
    private void generateLoanReport(List<User> users) {
        int totalLoans = 0;
        int approved = 0;
        int rejected = 0;
        int pending = 0;
        double totalAmount = 0;

        for (User user : users) {
            if (user instanceof Customer) {
                Customer customer = (Customer) user;

                for (Loan loan : customer.getLoans()) {
                    totalLoans++;
                    totalAmount += loan.getAmount();

                    switch (loan.getStatus()) {
                        case APPROVED:
                            approved++;
                            break;
                        case REJECTED:
                            rejected++;
                            break;
                        case PENDING:
                            pending++;
                            break;
                    }
                }
            }
        }

        System.out.println("\n=== LOAN PERFORMANCE REPORT ===");
        System.out.println("Total Loans: " + totalLoans);
        System.out.println("Approved: " + approved);
        System.out.println("Rejected: " + rejected);
        System.out.println("Pending: " + pending);
        System.out.println("Total Loan Amount: " + totalAmount);

        double approveRate = totalLoans > 0 ? (approved * 100.0 / totalLoans) : 0;
        System.out.println("Approval Rate: " + approveRate + "%");
    }
}