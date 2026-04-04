package model;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

// Inheritance
public class Customer extends User implements Serializable {
    // Prevent Serialization errors
    private static final long serialVersionUID = 1L;

    // Declaration of ArrayLists of Account, Loan, Notification objects
    private List<Account> accounts;
    private List<Loan> loans;
    private List<Notification> notifications;
    private List<BillPayment> billPayments;

    // Customer inherits the attributes from the User
    public Customer(int userId, String name, String email, String password) {
        // Call parent constructor from User
        super(userId, name, email, password);

        // Initialize lists
        accounts = new ArrayList<>();
        loans = new ArrayList<>();
        notifications = new ArrayList<>();
        billPayments = new ArrayList<>();
    }

    // Add a new account
    public void addAccount(Account account) {
        accounts.add(account);
    }

    // Apply Loan
    public void applyLoan(Loan loan) {
        loans.add(loan);
    }

    // Pay Bills
    public void payBill(BillPayment billPayment) {
        if (billPayments == null) {
            billPayments = new ArrayList<>();
        }
        billPayments.add(billPayment);
    }

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

    // Get Bills
    public List<BillPayment> getBillPayments() {
        if (billPayments == null) {
            billPayments = new ArrayList<>();
        }
        return new ArrayList<>(billPayments);
    }

    // Add a Loan
    public void addLoan(Loan loan) {
        loans.add(loan);
    }

    // Add a notification
    public void addNotification(Notification notification) {
        notifications.add(notification);
    }

    // Notify user
    public void notifyUser(String message, Notification.NotificationType type) {
        Notification notification = new Notification(message, type);
        notifications.add(notification);
    }
}

/*
 DESIGN DECISIONS & OOP PRINCIPLES:

 1. INHERITANCE:
    - Customer extends User, reusing common attributes (id, name, email, password).

 2. COMPOSITION:
    - Customer "has-a" relationship with:
        Accounts, Loans, Notifications, BillPayments.
    - This models real-world banking relationships.

 3. ENCAPSULATION:
    - Internal lists are private and exposed via safe copies.

 4. SINGLE RESPONSIBILITY:
    - Responsible only for managing customer-related data.
    - Business logic is handled in service layer.

 5. ABSTRACTION:
    - Methods like applyLoan(), notifyUser() hide implementation details.

 6. EXTENSIBILITY:
    - Additional features (e.g., credit score, KYC) can be added easily.
*/
