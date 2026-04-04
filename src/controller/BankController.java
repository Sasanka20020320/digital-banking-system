package controller;

import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.*;
import service.*;

import javax.swing.*;

public class BankController {
    private AuthService authService;
    private TransactionService transactionService;
    private LoanService loanService;
    private BillPaymentService billService;

    private FraudDetectionService fraudService = new FraudDetectionService();

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
    public int generateAccountNumber(List<User> users) {
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

    // Generate Monthly Statement
    public void generateMonthlyStatement(Customer customer, JFrame parent) {
        // Select account
        String[] accountNumbers = customer.getAccounts().stream()
                .map(acc -> String.valueOf(acc.getAccountNumber()))
                .toArray(String[]::new);

        JComboBox<String> accountDropdown = new JComboBox<>(accountNumbers);

        JTextField monthField = new JTextField();
        JTextField yearField = new JTextField();

        Object[] message = {
                "Select Account:", accountDropdown,
                "Month (1-12):", monthField,
                "Year (YYYY):", yearField
        };

        int option = JOptionPane.showConfirmDialog(parent, message,
                "Generate Monthly Statement", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                int accNo = Integer.parseInt((String) accountDropdown.getSelectedItem());
                int month = Integer.parseInt(monthField.getText());
                int year = Integer.parseInt(yearField.getText());

                Account account = findAccount(customer, accNo);
                if (account == null) {
                    JOptionPane.showMessageDialog(parent, "Account not found!");
                    return;
                }

//                String statement = account.generateMonthlyStatement(month, year);
                customer.notifyUser("Monthly statement generated for account " + accNo,
                        Notification.NotificationType.INFO);

                // Prepare table data
                List<Transaction> transactions = account.getTransactions();
                List<Transaction> filtered = new ArrayList<>();
                for (Transaction t : transactions) {
                    LocalDate date = t.getTimestamp().atZone(ZoneId.systemDefault()).toLocalDate();
                    if (date.getMonthValue() == month && date.getYear() == year) {
                        filtered.add(t);
                    }
                }

                String[] columnNames = {"Date", "Type", "Amount"};
                Object[][] data = new Object[filtered.size()][3];

                for (int i = 0; i < filtered.size(); i++) {
                    Transaction t = filtered.get(i);
                    LocalDate date = t.getTimestamp().atZone(ZoneId.systemDefault()).toLocalDate();
                    data[i][0] = date.toString();
                    data[i][1] = t.getType().toString();
                    data[i][2] = t.getAmount();
                }

                JTable table = new JTable(data, columnNames) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false; // All cells non-editable
                    }
                };

                JScrollPane scrollPane = new JScrollPane(table);
                scrollPane.setPreferredSize(new Dimension(500, 400));

                JOptionPane.showMessageDialog(parent, scrollPane,
                        "Monthly Statement - Account " + accNo, JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Error: " + ex.getMessage());
            }
        }
    }

    // Get All Loans
    public List<Loan> getAllLoans(List<User> users, List<Customer> loanCustomers) {
        List<Loan> allLoans = new ArrayList<>();

        for (User user : users) {
            if (user instanceof Customer) {
                Customer c = (Customer) user;

                for (Loan loan : c.getLoans()) {
                    allLoans.add(loan);
                    loanCustomers.add(c);
                }
            }
        }

        return allLoans;
    }

    // Action methods
    // Deposit
    public void deposit(Customer customer, int accNo, double amount) throws Exception {
        Account acc = findAccount(customer, accNo);

        if (acc == null) {
            throw new Exception("Account not found");

        }

        if (acc.isClosed()) {
            throw new Exception("Account is closed");
        }

        if (acc.isFrozen()) {
            throw new Exception("Account is closed");
        }

        transactionService.deposit(acc, amount, customer);
    }

    // Withdraw
    public void withdraw(Customer customer, int accNo, double amount) throws Exception {
        Account acc = findAccount(customer, accNo);

        if (acc == null) {
            throw new Exception("Account not found");
        }

        if (acc.isClosed()) {
            throw new Exception("Account is closed");
        }

        if (acc.isFrozen()) {
            throw new Exception("Account is closed");
        }

        try {
            transactionService.withdraw(acc, amount, customer);
        } catch (Exception e) {
            customer.notifyUser("Withdrawal failed: " + e.getMessage(), Notification.NotificationType.ALERT);
            System.out.println(e.getMessage());
        }
    }

    // Transfer
    public void transfer(Customer customer, List<User> users, int accFrom, int accTo, double amount) throws Exception {
        Account from = findAccount(customer, accFrom);
        Account to = findAccountGlobal(users, accTo);

        if (from == null || to == null) {
            throw new Exception("Account(s) not found");
        }

        if (from.isClosed() || to.isClosed()) {
            throw new Exception("One or more accounts are closed");
        }

        if (from.isFrozen() || to.isFrozen()) {
            throw new Exception("One or more accounts are closed");
        }

        transactionService.transfer(from, to, amount, customer);
    }

    // Apply Loan
    public void applyLoan(Customer customer, double amount, int months) throws Exception {
        if (amount <= 0) {
            throw new Exception("Invalid loan amount");
        }
        double rate = 0.12;

        Loan loan = new Loan(generateLoanId(customer), amount, customer.getUserId(), rate, months);
        customer.applyLoan(loan);

        customer.notifyUser("Loan request submitted: " + amount, Notification.NotificationType.INFO);

        System.out.println("Loan request submitted. Waiting for approval...");
    }

    // Pay Bills GUI
    public void payBillGUI(Customer customer, int accNo, String billType, double amount) throws Exception {
        Account acc = findAccount(customer, accNo);

        if (acc == null) {
            throw new Exception("Account not found");
        }

        if (acc.isClosed()) {
            throw new Exception("Account is closed");
        }

        if (acc.isFrozen()) {
            throw new Exception("Account is frozen");
        }

        billService.payBill(acc, billType, amount, customer);
    }

    // Open new Account
    public Account createAccount(Customer customer, String type, double balance, double extra, List<User> users) {
        int accNo = generateAccountNumber(users);
        Account newAcc = null;

        switch (type) {
            case "Savings":
                newAcc = new SavingsAccount(accNo, balance, 0.05);
                break;
            case "Checking":
                newAcc = new CheckingAccount(accNo, balance, extra); // extra = overdraft
                break;
            case "Student":
                newAcc = new StudentAccount(accNo, balance);
                break;
            case "Fixed Deposit":
                newAcc = new FixedDepositAccount(accNo, balance, 0.2, (int) extra); // extra = months
                break;
            default:
                throw new IllegalArgumentException("Invalid account type");
        }

        customer.addAccount(newAcc);
        customer.notifyUser("New account created: " + accNo, Notification.NotificationType.INFO);

        return newAcc;
    }

    // Pay Loan Installment
    public void payLoanInstallment(Customer customer, Loan loan, Account acc, double amount) {
        loanService.payInstallment(loan, acc, customer, amount);
        customer.notifyUser("Paid installment for Loan ID: " + loan.getLoanId(), Notification.NotificationType.INFO);
    }

    // viewMonthlyStatements
    public void viewMonthlyStatements(Customer customer, JFrame parent) {
        List<Account> accounts = customer.getAccounts();
        if (accounts.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No accounts found.");
            return;
        }

        // Select account
        String[] accountNumbers = accounts.stream()
                .map(acc -> String.valueOf(acc.getAccountNumber()))
                .toArray(String[]::new);
        JComboBox<String> accDropdown = new JComboBox<>(accountNumbers);
        int accChoice = JOptionPane.showConfirmDialog(parent, accDropdown, "Select Account",
                JOptionPane.OK_CANCEL_OPTION);
        if (accChoice != JOptionPane.OK_OPTION) return;

        int accNo = Integer.parseInt((String) accDropdown.getSelectedItem());
        Account account = findAccount(customer, accNo);

        if (account == null) {
            JOptionPane.showMessageDialog(parent, "Account not found!");
            return;
        }

        List<String> statements = account.getMonthlyStatements();
        if (statements.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No statements available.");
            return;
        }

        // Select last 3 statements (or fewer)
        int start = Math.max(0, statements.size() - 3); // index of first of last 3
        int count = statements.size() - start;          // number of statements to show

        String[] statementTitles = new String[count];

        //Last statement is "Statement 1 - Counts backwards"
        for (int i = 0; i < count; i++) {
            statementTitles[i] = "Statement " + (i + 1);
        }

        JComboBox<String> stmtDropdown = new JComboBox<>(statementTitles);
        int stmtChoice = JOptionPane.showConfirmDialog(parent, stmtDropdown, "Select Statement",
                JOptionPane.OK_CANCEL_OPTION);
        if (stmtChoice != JOptionPane.OK_OPTION) return;

//        int stmtIndex = start + (count - 1 - stmtDropdown.getSelectedIndex());
//        String selectedStatement = statements.get(stmtIndex);

        // Build table from statement transactions
        List<Transaction> transactions = account.getTransactions();
        String[] columnNames = {"Date", "Type", "Amount"};
        Object[][] data = new Object[transactions.size()][3];

        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            LocalDate date = t.getTimestamp().atZone(ZoneId.systemDefault()).toLocalDate();
            data[i][0] = date.toString();
            data[i][1] = t.getType().toString();
            data[i][2] = t.getAmount();
        }

        JTable table = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // make read-only
            }
        };

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(parent, scrollPane,
                "Monthly Statement - Account " + accNo, JOptionPane.INFORMATION_MESSAGE);
    }

    // Accept Loan
    public void approveLoan(Loan loan, Customer customer, Staff staff) {
        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new IllegalStateException("Loan already processed");
        }

        staff.approveLoan(loan, customer.getAccounts().get(0), customer, loanService);

        customer.notifyUser("Loan Approved! Amount credited.", Notification.NotificationType.INFO);
    }

    // Reject Loan
    public void rejectLoan(Loan loan, Staff staff) {
        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new IllegalStateException("Loan already processed");
        }

        staff.rejectLoan(loan, loanService);
    }

    // Get all users
    public Object[][] getAllUsersData(List<User> users) {
        Object[][] data = new Object[users.size()][3];

        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            data[i][0] = u.getUserId();
            data[i][1] = u.getName();
            data[i][2] = u.getEmail();
        }

        return data;
    }

    // Get all accounts
    public Object[][] getAllAccountsData(List<User> users) {
        List<Object[]> rows = new ArrayList<>();

        for (User user : users) {
            if (user instanceof Customer) {
                Customer c = (Customer) user;

                for (Account acc : c.getAccounts()) {
                    rows.add(new Object[]{
                            c.getName(),
                            acc.getAccountNumber(),
                            acc.getBalance(),
                            acc.isFrozen() ? "Yes" : "No",
                            acc.isClosed() ? "Yes" : "No"
                    });
                }
            }
        }

        return rows.toArray(new Object[0][]);
    }

    // Get all loans
    public Object[][] getAllLoansData(List<User> users) {
        List<Object[]> rows = new ArrayList<>();

        for (User user : users) {
            if (user instanceof Customer) {
                Customer c = (Customer) user;

                for (Loan loan : c.getLoans()) {
                    rows.add(new Object[]{
                            loan.getLoanId(),
                            loan.getAmount(),
                            loan.getStatus(),
                            c.getName()
                    });
                }
            }
        }

        return rows.toArray(new Object[0][]);
    }

    // Loan Performance Reports
    public void showLoanReport(List<User> users, JFrame parent) {
        int totalLoans = 0, approved = 0, rejected = 0, pending = 0;
        double totalAmount = 0;

        for (User user : users) {
            if (user instanceof Customer c) {
                for (Loan loan : c.getLoans()) {
                    totalLoans++;
                    totalAmount += loan.getAmount();

                    switch (loan.getStatus()) {
                        case APPROVED -> approved++;
                        case REJECTED -> rejected++;
                        case PENDING -> pending++;
                    }
                }
            }
        }

        double approveRate = totalLoans > 0 ? (approved * 100.0 / totalLoans) : 0;

        String message =
                "=== LOAN PERFORMANCE REPORT ===\n\n" +
                        "Total Loans: " + totalLoans + "\n" +
                        "Approved: " + approved + "\n" +
                        "Rejected: " + rejected + "\n" +
                        "Pending: " + pending + "\n\n" +
                        "Total Amount: " + totalAmount + "\n" +
                        "Approval Rate: " + String.format("%.2f", approveRate) + "%";

        JOptionPane.showMessageDialog(parent, message);
    }

    // Get suspicious transactions
    public List<Object[]> getSuspiciousTransactions(List<User> users) {
        List<Object[]> data = new ArrayList<>();

        for (User user : users) {
            if (user instanceof Customer) {
                Customer c = (Customer) user;

                for (Account acc : c.getAccounts()) {
                    for (Transaction t : acc.getTransactions()) {
                        if (t.isSuspicious()) {

                            data.add(new Object[]{
                                    c.getName(),
                                    acc.getAccountNumber(),
                                    t.getType(),
                                    t.getAmount(),
                                    t.getTimestamp()
                            });
                        }
                    }
                }
            }
        }

        return data;
    }

    // Update user info
    public void updateUserGUI(List<User> users, JFrame parent) {

        String[] userOptions = users.stream()
                .map(u -> u.getUserId() + " - " + u.getName())
                .toArray(String[]::new);

        JComboBox<String> userDropdown = new JComboBox<>(userOptions);

        int select = JOptionPane.showConfirmDialog(parent, userDropdown,
                "Select User", JOptionPane.OK_CANCEL_OPTION);

        if (select != JOptionPane.OK_OPTION) return;

        int index = userDropdown.getSelectedIndex();
        User selectedUser = users.get(index);

        String[] actions = {"Name", "Email", "Password"};
        JComboBox<String> actionDropdown = new JComboBox<>(actions);

        JTextField inputField = new JTextField();

        Object[] message = {
                "Update:", actionDropdown,
                "New Value:", inputField
        };

        int option = JOptionPane.showConfirmDialog(parent, message,
                "Update User", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String value = inputField.getText();

            switch (actionDropdown.getSelectedItem().toString()) {
                case "Name" -> selectedUser.setName(value);

                case "Email" -> {
                    boolean exists = users.stream()
                            .anyMatch(u -> u.getEmail().equalsIgnoreCase(value)
                                    && u.getUserId() != selectedUser.getUserId());

                    if (exists) {
                        JOptionPane.showMessageDialog(parent, "Email already exists!");
                        return;
                    }
                    selectedUser.setEmail(value);
                }

                case "Password" -> selectedUser.setPassword(value);
            }

            JOptionPane.showMessageDialog(parent, "User updated successfully!");
        }
    }

    // Freeze / Close Account
    public void manageAccountStatusGUI(List<User> users, JFrame parent) {

        List<Customer> customers = users.stream()
                .filter(u -> u instanceof Customer)
                .map(u -> (Customer) u)
                .toList();

        if (customers.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No customers found!");
            return;
        }

        // Select customer
        String[] custOptions = customers.stream()
                .map(c -> c.getName() + " (ID: " + c.getUserId() + ")")
                .toArray(String[]::new);

        JComboBox<String> custDropdown = new JComboBox<>(custOptions);

        int cOpt = JOptionPane.showConfirmDialog(parent, custDropdown,
                "Select Customer", JOptionPane.OK_CANCEL_OPTION);

        if (cOpt != JOptionPane.OK_OPTION) return;

        Customer selectedCustomer = customers.get(custDropdown.getSelectedIndex());

        if (selectedCustomer.getAccounts().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No accounts found!");
            return;
        }

        // Select account
        String[] accOptions = selectedCustomer.getAccounts().stream()
                .map(acc -> "Acc: " + acc.getAccountNumber())
                .toArray(String[]::new);

        JComboBox<String> accDropdown = new JComboBox<>(accOptions);

        int aOpt = JOptionPane.showConfirmDialog(parent, accDropdown,
                "Select Account", JOptionPane.OK_CANCEL_OPTION);

        if (aOpt != JOptionPane.OK_OPTION) return;

        Account selectedAccount =
                selectedCustomer.getAccounts().get(accDropdown.getSelectedIndex());

        // Action
        String[] actions = {"Freeze", "Close"};
        JComboBox<String> actionDropdown = new JComboBox<>(actions);

        int actionOpt = JOptionPane.showConfirmDialog(parent, actionDropdown,
                "Select Action", JOptionPane.OK_CANCEL_OPTION);

        if (actionOpt != JOptionPane.OK_OPTION) return;

        String action = actionDropdown.getSelectedItem().toString();

        if (action.equals("Freeze")) {
            selectedAccount.setFrozen(true);

            selectedCustomer.notifyUser(
                    "Account " + selectedAccount.getAccountNumber() + " frozen by admin",
                    Notification.NotificationType.ALERT
            );

        } else {
            selectedAccount.setClosed(true);

            selectedCustomer.notifyUser(
                    "Account " + selectedAccount.getAccountNumber() + " closed by admin",
                    Notification.NotificationType.ALERT
            );
        }

        JOptionPane.showMessageDialog(parent, "Action completed successfully!");
    }

    // Export compliance reports
    public void exportComplianceReport(List<User> users, JFrame parent) {
        String fileName = "compliance_report.csv";

        try (java.io.PrintWriter writer = new java.io.PrintWriter(fileName)) {

            writer.println("Customer Name,Account No,Transaction Type,Amount,Suspicious");

            for (User user : users) {
                if (user instanceof Customer) {
                    Customer c = (Customer) user;

                    for (Account acc : c.getAccounts()) {
                        for (Transaction t : acc.getTransactions()) {

                            writer.println(
                                    c.getName() + "," +
                                            acc.getAccountNumber() + "," +
                                            t.getType() + "," +
                                            t.getAmount() + "," +
                                            t.isSuspicious()
                            );
                        }
                    }
                }
            }

            JOptionPane.showMessageDialog(parent,
                    "Report exported successfully!\nSaved as: " + fileName);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    "Error exporting report: " + e.getMessage());
        }
    }
}

/*
==================== UPDATED DESIGN DECISIONS - BANK CONTROLLER ====================

1. ARCHITECTURE (MVC PATTERN)
   - The system now follows a Model-View-Controller (MVC) architecture:
        Model      → Entities (User, Account, Loan, Transaction)
        View       → GUI (BankGUI using Swing)
        Controller → BankController
   - This ensures clear separation between UI, business logic, and data.

2. LAYERED DESIGN
   - The application is divided into layers:
        GUI Layer        → Handles user interface (Swing)
        Controller Layer → Handles user actions and flow logic
        Service Layer    → Handles business logic
        Model Layer      → Represents data
   - Improves maintainability and scalability.

3. SEPARATION OF CONCERNS
   - BankController:
        - Handles UI interaction logic
        - Validates inputs
        - Delegates operations to services
   - Services:
        - Contain core business rules (transactions, loans, fraud detection)
   - Models:
        - Store and manage data

4. DEPENDENCY INJECTION
   - Services are injected via constructor:
        AuthService, TransactionService, LoanService, BillPaymentService
   - Benefits:
        - Loose coupling
        - Easier testing
        - Better modularity

5. ENCAPSULATION
   - All model fields are private and accessed via getters/setters.
   - Controller uses helper methods (findAccount, generateAccountNumber)
     to avoid direct data manipulation.

6. ABSTRACTION
   - Uses abstract types like User and Account.
   - Controller does not depend on specific implementations
     (SavingsAccount, CheckingAccount, etc.).

7. POLYMORPHISM
   - Runtime polymorphism via instanceof:
        - Customer, Staff, Admin
   - Different dashboards are shown based on role.

8. ROLE-BASED ACCESS CONTROL
   - System enforces permissions:
        Customer → transactions, loans, bills
        Staff    → loan approval/rejection
        Admin    → system control, reports, compliance
   - Mimics real-world banking systems.

9. SINGLE RESPONSIBILITY PRINCIPLE (SRP)
   - Each method performs one task:
        deposit(), transfer(), approveLoan(), etc.
   - GUI methods handle only UI logic
   - Services handle only business logic

10. REUSABILITY
   - Common helper methods:
        findAccount(), findAccountGlobal(), generateAccountNumber()
   - Reduces code duplication.

11. DATA VALIDATION & SAFETY
   - Checks implemented before operations:
        - Account existence
        - Frozen/Closed status
        - Duplicate emails
   - Prevents invalid system states.

12. EVENT-DRIVEN PROGRAMMING (GUI)
   - Uses ActionListeners for handling user actions.
   - UI responds dynamically to events (button clicks, selections).

13. DATA PERSISTENCE
   - FileService is used to:
        - Load users at startup
        - Save users after operations
   - Ensures data is not lost between sessions.

14. REPORTING & ANALYTICS
   - System generates:
        - Loan Performance Reports
        - Suspicious Transaction Reports
        - Compliance Reports (CSV export)
   - Helps simulate real banking analytics systems.

15. SCHEDULER / AUTOMATION
   - SchedulerService handles:
        - Monthly interest calculation
        - Loan reminders
        - Bill reminders
        - Monthly statement generation
   - Introduces background system automation.

16. ERROR HANDLING
   - Uses try-catch blocks in GUI and controller
   - Prevents system crashes and improves user experience.

17. EXTENSIBILITY
   - Easy to extend:
        - Add new account types
        - Add new reports
        - Add new services
   - Minimal changes required in existing code.

18. DESIGN TRADE-OFFS
   - Uses instanceof instead of advanced patterns (Strategy, Factory)
   - Chosen for simplicity and readability (academic context)
   - Can be upgraded later for enterprise-level scalability.
*/