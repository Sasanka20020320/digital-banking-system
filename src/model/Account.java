package model;

import exception.InsufficientBalanceException;
import exception.InvalidAmountException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Account implements Serializable {
    // Prevent Serialization errors
    private static final long serialVersionUID = 1L;

    // Parent attributes
    private int accountNumber;
    private double balance;
    protected double minimumBalance = 1000;

    private boolean frozen = false;
    private boolean closed = false;

    // ArrayList to store transactions
    private List<Transaction> transactions;

    // Store monthly statements
    private List<String> monthlyStatements;

    // Parent constructor for all account types
    public Account(int accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;

        // Initialize Transactions list
        transactions = new ArrayList<>();
        monthlyStatements = new ArrayList<>();
    }

    // Store monthly statements
    public void addMonthlyStatements(String statement) {
        if (monthlyStatements == null) {
            monthlyStatements = new ArrayList<>();
        }
        monthlyStatements.add(statement);
    }

    // Getters for private attributes
    public int getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        if(transactions == null) {
            transactions = new ArrayList<>();
        }
        return new ArrayList<>(transactions);
    }

    public List<String> getMonthlyStatements() {
        return new ArrayList<>(monthlyStatements);
    }

    public boolean isFrozen() {
        return frozen;
    }

    public boolean isClosed() {
        return closed;
    }

    // Setters for attributes
    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    protected void setBalance(double balance) { this.balance = balance; }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    // Transaction method
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    // Increase and decrease methods to use in withdrawals, deposits. Also to use in Transfer and Bill Payment transactions to update account balance
    public void increaseBalance(double amount) {
        balance += amount;
    }

    public void decreaseBalance(double amount) {
        balance -= amount;
    }

    // Abstraction method to access minimumBalance private attribute
    public boolean canWithdraw(double amount) {
        return (balance - amount) >= minimumBalance;
    }

    // Deposit method
    public void deposit(double amount) {
        if(amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive");
        }
        increaseBalance(amount);

        // Store the transaction
        Transaction transaction = new Transaction(amount, TransactionType.DEPOSIT);
        addTransaction(transaction);
    }

    // Withdraw method for (Bill Payment etc.)
    public void withdraw(double amount, TransactionType type) {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdraw amount must be positive");
        }

        if (!canWithdraw(amount)) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        decreaseBalance(amount);

        // Store the transaction
        Transaction transaction = new Transaction(amount, type);
        addTransaction(transaction);
    }

    // Normal Withdraw
    public void withdraw(double amount) {
        withdraw(amount, TransactionType.WITHDRAW);
    }

    // Monthly Statement Generation
    public String generateMonthlyStatement(int month, int year) {
        if (monthlyStatements == null) {
            monthlyStatements = new ArrayList<>();
        }

        // Prevent duplicates
        for (String stmt : monthlyStatements) {
            if (stmt.contains("Month?Year: " + month + "/" + year)) {
                return stmt;
            }
        }

        double startingBalance = getStartingBalance(month, year);
        double endingBalance = getBalance();
        double totalInterest = calculateInterest(month, year);

        StringBuilder sb = new StringBuilder();
        sb.append("=== Monthly Statement ===\n");
        sb.append("Account No: ").append(accountNumber).append("\n");
        sb.append("Month/Year: ").append(month).append("/").append(year).append("\n");
        sb.append("Starting Balance: ").append(startingBalance).append("\n");
        sb.append("Ending Balance: ").append(endingBalance).append("\n");
        sb.append("Interest Earned: ").append(totalInterest).append("\n");
        sb.append("Transactions:\n");

        for (Transaction t : transactions) {
            Instant ts = t.getTimestamp();

            // Filter transactions in the requested month/year
            LocalDate date = ts.atZone(ZoneId.systemDefault()).toLocalDate();

            if (date.getMonthValue() == month && date.getYear() == year) {
                sb.append(t).append("\n");
            }
        }

        String statements = sb.toString();

        addMonthlyStatements(sb.toString());

        return sb.toString();
    }

    // Calculate interest
    private double calculateInterest(int month, int year) {
        // Calculate interest earned in the month
        double interest = 0;
        if (this instanceof SavingsAccount) {
            interest = getBalance() * ((SavingsAccount) this).getInterestRate() / 12;
        }
        return interest;
    }

    // Starting Balance
    private double getStartingBalance(int month, int year) {
        double startingBalance = balance;

        for (Transaction t : transactions) {
            LocalDate date = t.getTimestamp().atZone(ZoneId.systemDefault()).toLocalDate();
            if (date.getYear() > year || (date.getYear() == year && date.getMonthValue() > month)) {
                startingBalance -= t.getAmount();
            }
        }
        return startingBalance;
    }

    @Override
    public String toString() {
        return "Account No: " + accountNumber + ", Balance: " + balance;
    }
}

/*
 DESIGN DECISIONS & OOP PRINCIPLES:

 1. ABSTRACTION:
    - The Account class provides common banking operations such as deposit(), withdraw(),
      and transaction handling without exposing internal implementation details.
    - Users interact with high-level methods instead of directly modifying balance.

 2. ENCAPSULATION:
    - Fields like balance, transactions, and accountNumber are private.
    - Controlled access is provided via getters and protected setters.
    - This ensures data integrity (e.g., balance cannot be modified directly).

 3. INHERITANCE:
    - Account acts as a base class for specialized accounts:
        SavingsAccount, CheckingAccount, StudentAccount, FixedDepositAccount.
    - Common functionality is reused, reducing code duplication.

 4. POLYMORPHISM:
    - Methods like withdraw() are overridden in subclasses (e.g., CheckingAccount, FixedDepositAccount)
      to provide different behaviors such as overdraft or penalties.

 5. SINGLE RESPONSIBILITY PRINCIPLE:
    - This class handles only account-related operations (balance, transactions, statements),
      while services (TransactionService, FraudDetectionService) handle business logic.

 6. DATA SAFETY:
    - Returns copies of lists (new ArrayList<>(transactions)) to prevent external modification.

 7. EXTENSIBILITY:
    - New account types can be added easily without modifying existing code.

 8. ADDITIONAL DESIGN:
    - frozen and closed flags allow admin control without deleting accounts.
    - monthlyStatements supports report generation without recalculating repeatedly.
*/
