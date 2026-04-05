package gui;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

import controller.BankController;
import service.*;
import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BankGUI extends JFrame {
    private BankController controller;
    private List<User> users;
    private FileService fileService;

    public BankGUI() {
        fileService = new FileService();
        users = fileService.loadUsers();

        if (users.isEmpty()) {
            Customer customer = new Customer(1, "Sasanka", "sasanka@email.com", "1234");
            Staff staff = new Staff(2, "Wishwa", "wishwa@email.com", "1234");
            Admin admin = new Admin(3, "Admin", "admin@email.com", "1234");

            users.add(customer);
            users.add(staff);
            users.add(admin);

            Account acc1 = new SavingsAccount(101, 5000, 0.1);
            Account acc2 = new StudentAccount(102, 3000);
            Account acc3 = new CheckingAccount(103, 1000, -1000);
            Account acc4 = new FixedDepositAccount(104, 10000, 0.2, 12);

            customer.addAccount(acc1);
            customer.addAccount(acc2);
            customer.addAccount(acc3);
            customer.addAccount(acc4);

            fileService.saveUsers(users);
        }

        // Services
        TransactionService ts = new TransactionService();
        LoanService loanService = new LoanService();
        AuthService authService = new AuthService();
        BillPaymentService billService = new BillPaymentService();
        SchedulerService scheduler = new SchedulerService();

        try {
            scheduler.run(users);
        } catch (Exception e) {
            System.out.println("Scheduler error: " + e.getMessage());
        }

        controller = new BankController(authService, ts, loanService, billService);

        setTitle("Rajarata Digital Banking System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Rajarata Digital Banking System", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        // Buttons
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        JPanel panel = new JPanel();
        panel.add(loginBtn);
        panel.add(registerBtn);

        add(title, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        // BUTTON ACTIONS
        loginBtn.addActionListener(e -> showLoginDialog());
        registerBtn.addActionListener(e -> showRegisterDialog());

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to exit?",
                        "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    fileService.saveUsers(users);
                    System.exit(0);
                }
            }
        });
    }

    // Account Dropdown
    private JComboBox<Integer> getAccountDropdown(Customer customer) {
        JComboBox<Integer> comboBox = new JComboBox<>();

        for (Account acc : customer.getAccounts()) {
            comboBox.addItem(acc.getAccountNumber());
        }

        return comboBox;
    }

    // LOGIN
    private void showLoginDialog() {
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
                "Email:", emailField,
                "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "Login",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            User user = controllerLogin(email, password);

            if (user != null) {
                JOptionPane.showMessageDialog(this, "Login successful!");

                if (user instanceof Customer) {
                    showCustomerDashboard((Customer) user);
                } else if (user instanceof Staff) {
                    showStaffDashboard((Staff) user);
                } else if (user instanceof Admin) {
                    showAdminDashboard((Admin) user);
                }

            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }
        }
    }

    private User controllerLogin(String email, String password) {
        AuthService authService = new AuthService();
        return authService.login(email, password, users);
    }

    // REGISTER
    private void showRegisterDialog() {
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
                "Name:", nameField,
                "Email:", emailField,
                "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "Register",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            int newId = controller.generateAccountNumber(users);

            Customer newCustomer = new Customer(newId, name, email, password);

            // Default account
            Account acc = new SavingsAccount(newId, 0, 0.05);
            newCustomer.addAccount(acc);

            users.add(newCustomer);
            fileService.saveUsers(users);

            JOptionPane.showMessageDialog(this, "Registration successful!");
        }
    }

    // CUSTOMER DASHBOARD
    private void showCustomerDashboard(Customer customer) {
        JFrame dashboard = new JFrame("Customer Dashboard");
        dashboard.setSize(500, 400);
        dashboard.setLocationRelativeTo(this);
        dashboard.setLayout(new GridLayout(0, 2, 10, 10));

        // Buttons
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton transferBtn = new JButton("Transfer");
        JButton loanBtn = new JButton("Apply Loan");
        JButton notificationBtn = new JButton("Notifications");
        JButton balanceBtn = new JButton("Check Balance");
        JButton billBtn = new JButton("Bill Payment");
        JButton transactionsBtn = new JButton("View Transactions");
        JButton openAccountBtn = new JButton("Open Account");
        JButton payLoanBtn = new JButton("Pay Loan Installment");
        JButton generateStatementBtn = new JButton("Generate a Statement");
        JButton viewStatementsBtn = new JButton("View Monthly Statements");
        JButton exitBtn = new JButton("Exit");

        dashboard.add(depositBtn);
        dashboard.add(withdrawBtn);
        dashboard.add(transferBtn);
        dashboard.add(loanBtn);
        dashboard.add(notificationBtn);
        dashboard.add(balanceBtn);
        dashboard.add(billBtn);
        dashboard.add(transactionsBtn);
        dashboard.add(openAccountBtn);
        dashboard.add(payLoanBtn);
        dashboard.add(generateStatementBtn);
        dashboard.add(viewStatementsBtn);
        dashboard.add(exitBtn);

        dashboard.setVisible(true);

        // ACTIONS
        // Deposit
        depositBtn.addActionListener(e -> {
            JComboBox<Integer> accDropdown = getAccountDropdown(customer);
            JTextField amountField = new JTextField();

            Object[] message = {
                    "Select Account:", accDropdown,
                    "Enter Amount:", amountField
            };

            int option = JOptionPane.showConfirmDialog(dashboard, message, "Deposit", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                try {
                    int accNo = (int) accDropdown.getSelectedItem();
                    double amount = Double.parseDouble(amountField.getText());

                    controller.deposit(customer, accNo, amount);
                    fileService.saveUsers(users);

                    JOptionPane.showMessageDialog(dashboard, "Deposit successful!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dashboard, "Error: " + ex.getMessage());
                }
            }
        });

        // Withdraw
        withdrawBtn.addActionListener(e -> {
            JComboBox<Integer> accDropdown = getAccountDropdown(customer);
            JTextField amountField = new JTextField();

            Object[] message = {
                    "Select Account:", accDropdown,
                    "Enter Amount:", amountField
            };

            int option = JOptionPane.showConfirmDialog(dashboard, message, "Withdraw", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                try {
                    int accNo = (int) accDropdown.getSelectedItem();
                    double amount = Double.parseDouble(amountField.getText());

                    controller.withdraw(customer, accNo, amount);
                    fileService.saveUsers(users);

                    JOptionPane.showMessageDialog(dashboard, "Withdraw successful!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dashboard, "Error: " + ex.getMessage());
                }
            }
        });

        // Transfer
        transferBtn.addActionListener(e -> {
            JComboBox<Integer> fromDropdown = getAccountDropdown(customer);
            JComboBox<Integer> toDropdown = new JComboBox<>();

            // Fill all accounts
            for (User user : users) {
                if (user instanceof Customer) {
                    Customer c = (Customer) user;
                    for (Account acc : c.getAccounts()) {
                        toDropdown.addItem(acc.getAccountNumber());
                    }
                }
            }

            JTextField amountField = new JTextField();

            Object[] message = {
                    "From Account:", fromDropdown,
                    "To Account:", toDropdown,
                    "Amount:", amountField
            };

            int option = JOptionPane.showConfirmDialog(dashboard, message, "Transfer", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                try {
                    int from = (int) fromDropdown.getSelectedItem();
                    int to = (int) toDropdown.getSelectedItem();
                    double amount = Double.parseDouble(amountField.getText());

                    if (from == to) {
                        JOptionPane.showMessageDialog(dashboard,
                                "Cannot transfer to the same account!");
                        return;
                    }

                    controller.transfer(customer, users, from, to, amount);
                    fileService.saveUsers(users);

                    JOptionPane.showMessageDialog(dashboard, "Transfer successful!");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dashboard, "Error: " + ex.getMessage());
                }
            }
        });

        // Apply Loan
        loanBtn.addActionListener(e -> {
            JTextField loanAmountField = new JTextField();
            JTextField durationField = new JTextField();

            Object[] message = {
                    "Loan Amount:", loanAmountField,
                    "Duration (Months):", durationField
            };

            int option = JOptionPane.showConfirmDialog(dashboard, message, "Apply Loan", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    double amount = Double.parseDouble(loanAmountField.getText());
                    int months = Integer.parseInt(durationField.getText());

                    controller.applyLoan(customer, amount, months); // Use the GUI-friendly method
                    JOptionPane.showMessageDialog(dashboard, "Loan request submitted successfully!");
                    fileService.saveUsers(users);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dashboard, "Please enter valid numbers!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dashboard, "Error: " + ex.getMessage());
                }
            }
        });

        // Notifications
        notificationBtn.addActionListener(e -> {

            List<Notification> notifications = customer.getNotifications();

            if (notifications.isEmpty()) {
                JOptionPane.showMessageDialog(dashboard, "No notifications available!");
                return;
            }

            // Table columns
            String[] columnNames = {"Type", "Message"};

            Object[][] data = new Object[notifications.size()][2];

            for (int i = 0; i < notifications.size(); i++) {
                Notification n = notifications.get(i);

                data[i][0] = n.getType();     // INFO / WARNING / ALERT
                data[i][1] = n.getMessage();  // actual message
            }

            JTable table = new JTable(data, columnNames);

            // Styling (optional but nice)
            table.setRowHeight(25);

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(500, 300));

            JOptionPane.showMessageDialog(
                    dashboard,
                    scrollPane,
                    "Notifications",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        // Check Balance
        balanceBtn.addActionListener(e -> {
            JComboBox<Integer> accDropdown = getAccountDropdown(customer);

            Object[] message = {
                    "Select Account:", accDropdown
            };

            int option = JOptionPane.showConfirmDialog(dashboard, message, "Check Balance", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                int accNo = (int) accDropdown.getSelectedItem();

                for (Account acc : customer.getAccounts()) {
                    if (acc.getAccountNumber() == accNo) {
                        JOptionPane.showMessageDialog(dashboard, "Balance: " + acc.getBalance());
                        return;
                    }
                }
            }
        });

        // Pay Bills
        billBtn.addActionListener(e -> {
            JComboBox<Integer> accDropdown = getAccountDropdown(customer);

            String[] billTypes = {"electricity", "water", "internet"};
            JComboBox<String> billTypeDropdown = new JComboBox<>(billTypes);

            JTextField amountField = new JTextField();

            Object[] message = {
                    "Select Account:", accDropdown,
                    "Bill Type:", billTypeDropdown,
                    "Amount:", amountField
            };

            int option = JOptionPane.showConfirmDialog(
                    dashboard,
                    message,
                    "Pay Bill",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (option == JOptionPane.OK_OPTION) {
                try {
                    int accNo = (int) accDropdown.getSelectedItem();
                    String billType = (String) billTypeDropdown.getSelectedItem();
                    double amount = Double.parseDouble(amountField.getText());

                    controller.payBillGUI(customer, accNo, billType, amount);

                    fileService.saveUsers(users);

                    JOptionPane.showMessageDialog(dashboard, "Bill paid successfully!");

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dashboard, "Invalid amount!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dashboard, "Error: " + ex.getMessage());
                }
            }
        });

        // View Transactions
        transactionsBtn.addActionListener(e -> {
            JComboBox<Integer> accDropdown = getAccountDropdown(customer);

            String[] filters = {"ALL", "DEPOSIT", "WITHDRAW", "TRANSFER", "BILL_PAYMENT", "LOAN_INSTALLMENT"};
            JComboBox<String> filterDropdown = new JComboBox<>(filters);

            Object[] message = {
                    "Select Account:", accDropdown,
                    "Filter:", filterDropdown
            };

            int option = JOptionPane.showConfirmDialog(
                    dashboard,
                    message,
                    "View Transactions",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (option == JOptionPane.OK_OPTION) {
                int accNo = (int) accDropdown.getSelectedItem();
                String selectedFilter = (String) filterDropdown.getSelectedItem();

                for (Account acc : customer.getAccounts()) {
                    if (acc.getAccountNumber() == accNo) {

                        List<Transaction> transactions = acc.getTransactions();

                        if (transactions.isEmpty()) {
                            JOptionPane.showMessageDialog(dashboard, "No transactions found!");
                            return;
                        }

                        // Table Columns
                        String[] columnNames = {"Date", "Type", "Amount", "Description"};

                        // Data (max 100 rows for safety)
                        Object[][] data = new Object[transactions.size()][4];

                        int row = 0;

                        for (Transaction t : transactions) {

                            if (selectedFilter.equals("ALL") ||
                                    t.getType().toString().equals(selectedFilter)) {


                                data[row][0] = t.getTimestamp();
                                data[row][1] = t.getType();
                                data[row][2] = t.getAmount();

                                // Description column
                                if (t.getType() == TransactionType.TRANSFER) {
                                    data[row][3] = "From: " + t.getFromAccount() + " → To: " + t.getToAccount();
                                } else {
                                    data[row][3] = t.getDescription();
                                }

                                row++;
                            }
                        }

                        // If no filtered results
                        if (row == 0) {
                            JOptionPane.showMessageDialog(dashboard, "No transactions for selected filter!");
                            return;
                        }

                        // Resize array to actual rows
                        Object[][] filteredData = new Object[row][4];
                        System.arraycopy(data, 0, filteredData, 0, row);

                        JTable table = new JTable(filteredData, columnNames);

                        JScrollPane scrollPane = new JScrollPane(table);
                        scrollPane.setPreferredSize(new Dimension(500, 300));

                        JOptionPane.showMessageDialog(
                                dashboard,
                                scrollPane,
                                "Transaction History",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        return;
                    }
                }

                JOptionPane.showMessageDialog(dashboard, "Account not found!");
            }
        });

        // Open New Account
        openAccountBtn.addActionListener(e -> {
            String[] accountTypes = {"Savings", "Checking", "Student", "Fixed Deposit"};
            JComboBox<String> typeDropdown = new JComboBox<>(accountTypes);
            JTextField balanceField = new JTextField();
            JTextField extraField = new JTextField(); // overdraft or months

            Object[] message = {
                    "Account Type:", typeDropdown,
                    "Initial Deposit:", balanceField,
                    "Extra - Optional (Overdraft / Months):", extraField
            };

            int option = JOptionPane.showConfirmDialog(
                    dashboard, message, "Open New Account", JOptionPane.OK_CANCEL_OPTION
            );

            if(option == JOptionPane.OK_OPTION) {
                try {
                    String type = (String) typeDropdown.getSelectedItem();
                    double balance = Double.parseDouble(balanceField.getText());
                    double extra = extraField.getText().isEmpty() ? 0 : Double.parseDouble(extraField.getText());

                    // Call controller to create the account
                    Account newAcc = controller.createAccount(customer, type, balance, extra, users);

                    // Save updated users
                    fileService.saveUsers(users);

                    JOptionPane.showMessageDialog(dashboard,
                            "Account created successfully!\nAccount No: " + newAcc.getAccountNumber()
                    );
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(dashboard, "Error: " + ex.getMessage());
                }
            }
        });

        // Pay loan installments
        payLoanBtn.addActionListener(e -> {
            List<Loan> loans = customer.getLoans();

            if (loans.isEmpty()) {
                JOptionPane.showMessageDialog(dashboard, "No loans found.");
                return;
            }

            // Loan dropdown
            String[] loanOptions = loans.stream()
                    .map(l -> "Loan ID: " + l.getLoanId() + " | Remaining: " + l.getRemainingBalance())
                    .toArray(String[]::new);
            JComboBox<String> loanDropdown = new JComboBox<>(loanOptions);

            // Account dropdown
            JComboBox<Integer> accDropdown = new JComboBox<>();
            for (Account acc : customer.getAccounts()) {
                accDropdown.addItem(acc.getAccountNumber());
            }

            Object[] message = {
                    "Select Loan:", loanDropdown,
                    "Select Account to pay from:", accDropdown
            };

            int option = JOptionPane.showConfirmDialog(dashboard, message, "Pay Loan Installment", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                try {
                    int loanIndex = loanDropdown.getSelectedIndex();
                    Loan selectedLoan = loans.get(loanIndex);

                    int accNo = (int) accDropdown.getSelectedItem();
                    Account selectedAcc = null;
                    for (Account acc : customer.getAccounts()) {
                        if (acc.getAccountNumber() == accNo) {
                            selectedAcc = acc;
                            break;
                        }
                    }

                    if (selectedAcc == null) throw new Exception("Account not found");

                    // Call controller
                    controller.payLoanInstallment(customer, selectedLoan, selectedAcc, selectedLoan.getMonthlyInstallment());

                    fileService.saveUsers(users);

                    JOptionPane.showMessageDialog(dashboard,
                            "Installment paid successfully for Loan ID: " + selectedLoan.getLoanId());

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dashboard, "Error: " + ex.getMessage());
                }
            }
        });

        // Generate a statement
        generateStatementBtn.addActionListener(e ->
                controller.generateMonthlyStatement(customer, dashboard));

        // View Monthly Statements
        viewStatementsBtn.addActionListener(e ->
                controller.viewMonthlyStatements(customer, dashboard));

        // Exit
        exitBtn.addActionListener(e -> dashboard.dispose());
    }

    // Staff dashboard
    private void showStaffDashboard(Staff staff) {
        JFrame dashboard = new JFrame("Staff Dashboard");
        dashboard.setSize(400, 300);
        dashboard.setLocationRelativeTo(this);
        dashboard.setLayout(new GridLayout(0, 1, 10, 10));

        JButton approveBtn = new JButton("Approve Loan");
        JButton rejectBtn = new JButton("Reject Loan");
        JButton suspiciousBtn = new JButton("View Suspicious Transactions");
        JButton exitBtn = new JButton("Exit");

        dashboard.add(approveBtn);
        dashboard.add(rejectBtn);
        dashboard.add(suspiciousBtn);
        dashboard.add(exitBtn);

        dashboard.setVisible(true);

        // ACTIONS
        // Accept Loan
        approveBtn.addActionListener(e -> {
            List<Customer> loanCustomers = new ArrayList<>();
            List<Loan> allLoans = controller.getAllLoans(users, loanCustomers);

            if (allLoans.isEmpty()) {
                JOptionPane.showMessageDialog(dashboard, "No loan requests available");
                return;
            }

            String[] loanOptions = new String[allLoans.size()];

            for (int i = 0; i < allLoans.size(); i++) {
                Loan l = allLoans.get(i);
                loanOptions[i] = "Loan ID: " + l.getLoanId() +
                        " | Amount: " + l.getAmount() +
                        " | Status: " + l.getStatus();
            }

            JComboBox<String> loanDropdown = new JComboBox<>(loanOptions);

            int option = JOptionPane.showConfirmDialog(
                    dashboard,
                    loanDropdown,
                    "Select Loan to Approve",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (option == JOptionPane.OK_OPTION) {
                try {
                    int index = loanDropdown.getSelectedIndex();
                    Loan selectedLoan = allLoans.get(index);
                    Customer loanOwner = loanCustomers.get(index);

                    controller.approveLoan(selectedLoan, loanOwner, staff);

                    fileService.saveUsers(users);

                    JOptionPane.showMessageDialog(dashboard, "Loan Approved!");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dashboard, "Error: " + ex.getMessage());
                }
            }
        });

        // Reject Loan
        rejectBtn.addActionListener(e -> {
            List<Customer> loanCustomers = new ArrayList<>();
            List<Loan> allLoans = controller.getAllLoans(users, loanCustomers);

            if (allLoans.isEmpty()) {
                JOptionPane.showMessageDialog(dashboard, "No loan requests available");
                return;
            }

            String[] loanOptions = new String[allLoans.size()];

            for (int i = 0; i < allLoans.size(); i++) {
                Loan l = allLoans.get(i);
                loanOptions[i] = "Loan ID: " + l.getLoanId() +
                        " | Amount: " + l.getAmount() +
                        " | Status: " + l.getStatus();
            }

            JComboBox<String> loanDropdown = new JComboBox<>(loanOptions);

            int option = JOptionPane.showConfirmDialog(
                    dashboard,
                    loanDropdown,
                    "Select Loan to Reject",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (option == JOptionPane.OK_OPTION) {
                try {
                    int index = loanDropdown.getSelectedIndex();
                    Loan selectedLoan = allLoans.get(index);

                    controller.rejectLoan(selectedLoan, staff);

                    fileService.saveUsers(users);

                    JOptionPane.showMessageDialog(dashboard, "Loan Rejected!");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dashboard, "Error: " + ex.getMessage());
                }
            }
        });

        // View Suspicious Transactions
        suspiciousBtn.addActionListener(e -> {

            List<Object[]> dataList = controller.getSuspiciousTransactions(users);

            if (dataList.isEmpty()) {
                JOptionPane.showMessageDialog(dashboard, "No suspicious transactions found!");
                return;
            }

            String[] columnNames = {
                    "Customer Name",
                    "Account No",
                    "Type",
                    "Amount",
                    "Timestamp"
            };

            Object[][] data = new Object[dataList.size()][5];

            for (int i = 0; i < dataList.size(); i++) {
                data[i] = dataList.get(i);
            }

            JTable table = new JTable(data, columnNames);

            // Styling
            table.setRowHeight(25);
            table.setFont(new Font("Arial", Font.PLAIN, 12));

            JTableHeader header = table.getTableHeader();
            header.setFont(new Font("Arial", Font.BOLD, 13));

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(700, 400));

            JOptionPane.showMessageDialog(
                    dashboard,
                    scrollPane,
                    "Suspicious Transactions",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        // Exit
        exitBtn.addActionListener(e -> dashboard.dispose());
    }

    private void showAdminDashboard(Admin admin) {
        JFrame dashboard = new JFrame("Admin Dashboard");
        dashboard.setSize(700, 500);
        dashboard.setLocationRelativeTo(this);
        dashboard.setLayout(new GridLayout(5, 2, 10, 10));

        JButton viewUsersBtn = new JButton("View All Users");
        JButton viewAccountsBtn = new JButton("View All Accounts");
        JButton viewLoansBtn = new JButton("View All Loans");
        JButton loanReportBtn = new JButton("Loan Report");
        JButton updateUserBtn = new JButton("Update User");
        JButton manageAccBtn = new JButton("Freeze / Close Account");
        JButton suspiciousBtn = new JButton("Suspicious Transactions");
        JButton exportBtn = new JButton("Export Report");
        JButton exitBtn = new JButton("Exit");

        dashboard.add(viewUsersBtn);
        dashboard.add(viewAccountsBtn);
        dashboard.add(viewLoansBtn);
        dashboard.add(loanReportBtn);
        dashboard.add(updateUserBtn);
        dashboard.add(manageAccBtn);
        dashboard.add(suspiciousBtn);
        dashboard.add(exportBtn);
        dashboard.add(exitBtn);

        dashboard.setVisible(true);

        // ACTIONS
        // Get All Users
        viewUsersBtn.addActionListener(e -> {
            String[] columns = {"User ID", "Name", "Email"};

            Object[][] data = controller.getAllUsersData(users);

            JTable table = new JTable(data, columns);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(600, 300));

            JOptionPane.showMessageDialog(dashboard, scrollPane, "All Users", JOptionPane.INFORMATION_MESSAGE);
        });

        // View All Accounts
        viewAccountsBtn.addActionListener(e -> {
            String[] columns = {"Customer", "Account No", "Balance", "Frozen", "Closed"};

            Object[][] data = controller.getAllAccountsData(users);

            JTable table = new JTable(data, columns);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(600, 300));

            JOptionPane.showMessageDialog(dashboard, scrollPane, "All Accounts", JOptionPane.INFORMATION_MESSAGE);
        });

        // View All Loans
        viewLoansBtn.addActionListener(e -> {
            String[] columns = {"Loan ID", "Amount", "Status", "Customer"};

            Object[][] data = controller.getAllLoansData(users);

            JTable table = new JTable(data, columns);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(600, 300));

            JOptionPane.showMessageDialog(dashboard, scrollPane, "All Loans", JOptionPane.INFORMATION_MESSAGE);
        });

        // View Loan Report
        loanReportBtn.addActionListener(e ->
                controller.showLoanReport(users, dashboard)
        );

        // Update User
        updateUserBtn.addActionListener(e ->
                controller.updateUserGUI(users, dashboard));

        // Manage Accounts
        manageAccBtn.addActionListener(e ->
                controller.manageAccountStatusGUI(users, dashboard));

        // View Suspicious Transactions
        suspiciousBtn.addActionListener(e -> {

            List<Object[]> dataList = controller.getSuspiciousTransactions(users);

            if (dataList.isEmpty()) {
                JOptionPane.showMessageDialog(dashboard, "No suspicious transactions found!");
                return;
            }

            String[] columnNames = {
                    "Customer Name",
                    "Account No",
                    "Type",
                    "Amount",
                    "Timestamp"
            };

            Object[][] data = new Object[dataList.size()][5];

            for (int i = 0; i < dataList.size(); i++) {
                data[i] = dataList.get(i);
            }

            JTable table = new JTable(data, columnNames);

            // Styling
            table.setRowHeight(25);
            table.setFont(new Font("Arial", Font.PLAIN, 12));

            JTableHeader header = table.getTableHeader();
            header.setFont(new Font("Arial", Font.BOLD, 13));

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(700, 400));

            JOptionPane.showMessageDialog(
                    dashboard,
                    scrollPane,
                    "Suspicious Transactions",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        // Export Compliance Reports
        exportBtn.addActionListener(e ->
                controller.exportComplianceReport(users, dashboard)
        );

        // Exit
        exitBtn.addActionListener(e -> dashboard.dispose());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BankGUI().setVisible(true);
        });
    }
}

/*
==================== DESIGN DECISIONS - BANK GUI ====================

1. PRESENTATION LAYER (GUI)
   - BankGUI represents the PRESENTATION layer of the system.
   - Built using Java Swing to provide a graphical interface.
   - Responsible for:
        - Displaying data (tables, dialogs)
        - Collecting user input
        - Triggering controller actions

2. MVC ARCHITECTURE
   - Follows Model-View-Controller (MVC):
        - Model → User, Account, Loan, Transaction
        - View → BankGUI (this class)
        - Controller → BankController
   - GUI does NOT handle business logic.
   - Delegates all operations to BankController.

3. SEPARATION OF CONCERNS
   - GUI handles:
        - Layout (JFrame, JPanel, JTable)
        - User interaction (buttons, dialogs)
   - Controller handles:
        - Logic (deposit, transfer, loan approval, etc.)
   - This keeps code clean and maintainable.

4. EVENT-DRIVEN PROGRAMMING
   - Uses ActionListener for button interactions.
   - Each button triggers a specific action.
   - Example:
        depositBtn → controller.deposit(...)
   - This is standard for GUI-based systems.

5. ROLE-BASED INTERFACE DESIGN
   - Different dashboards for different roles:
        - Customer → banking operations
        - Staff → loan management
        - Admin → system control & reports
   - Improves usability and security.

6. REUSABILITY
   - Common UI components reused:
        - getAccountDropdown() method
        - Table display patterns (JTable + JScrollPane)
   - Avoids duplication and improves consistency.

7. DATA PERSISTENCE HANDLING
   - FileService is used to:
        - Load data on startup
        - Save data after operations
   - Auto-save implemented:
        - After transactions
        - On application exit (window listener)

8. INITIAL DATA BOOTSTRAPPING
   - If no saved data exists:
        - Default users and accounts are created
   - Ensures system is usable on first launch.

9. ERROR HANDLING
   - Uses try-catch blocks for:
        - Input parsing (NumberFormatException)
        - Business logic errors
   - Errors are shown via JOptionPane dialogs.
   - Prevents application crashes.

10. USER EXPERIENCE (UX)
   - Uses:
        - Dialog boxes (JOptionPane)
        - Dropdowns (JComboBox)
        - Tables (JTable)
   - Provides:
        - Clear prompts
        - Immediate feedback messages
   - Enhances usability for non-technical users.

11. DATA VISUALIZATION
   - Tables (JTable) used for:
        - Transactions
        - Users
        - Accounts
        - Loans
   - Improves readability compared to console output.

12. SECURITY CONSIDERATIONS
   - Basic validation included:
        - Prevent same-account transfer
        - Prevent invalid inputs
   - Role-based dashboards restrict access.

13. EXTENSIBILITY
   - New features can be added easily:
        - Add new button
        - Call controller method
   - GUI structure supports scaling.

14. THREAD SAFETY
   - GUI initialized using:
        SwingUtilities.invokeLater()
   - Ensures execution on Event Dispatch Thread (EDT).
   - Prevents UI-related concurrency issues.

15. DESIGN TRADE-OFFS
   - Uses simple Swing instead of JavaFX for simplicity.
   - Direct use of JOptionPane instead of custom dialogs.
   - Suitable for:
        - Academic projects
        - Small-to-medium applications
*/