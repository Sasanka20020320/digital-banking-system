package model;

import java.util.ArrayList;
import java.util.List;

// Inheritance
public class Customer extends User {
    // Declaration of ArrayLists of Account, Loan, Notification objects
    private List<Account> accounts;
    private List<Loan> loans;
    private List<Notification> notifications;

    // Customer inherits the attributes from the User
    public Customer(int userId, String name, String email, String password) {
        // Call parent constructor from User
        super(userId, name, email, password);

        // Initialize lists
        accounts = new ArrayList<>();
        loans = new ArrayList<>();
        notifications = new ArrayList<>();
    }

    // Add a new account
    public void addAccount(Account account) {
        accounts.add(account);
    }

    // Apply Loan
    public void applyLoan() {}

    // Pay Bills
    public void payBill() {}

    // Get accounts
    public List<Account> getAccounts() {
        return new ArrayList<>(accounts);
    }

    // Get Loans
    public List<Loan> getLoans() {
        return new ArrayList<>(loans);
    }

    // View Balance
    public void viewBalance() {}

    // Get Notifications
    public List<Notification> getNotifications() {
        return new ArrayList<>(notifications);
    }

    // Add a Loan
    public void addLoan(Loan loan) {
        loans.add(loan);
    }

    // Add a notification
    public void addNotification(Notification notification) {
        notifications.add(notification);
    }
}
