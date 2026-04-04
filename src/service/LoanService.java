package service;

import model.*;

import java.util.List;

public class LoanService {
    public void approveLoan(Loan loan, Account account, Customer customer) {
        loan.setStatus(LoanStatus.APPROVED);
        account.deposit(loan.getAmount());

        customer.notifyUser("Loan approved: " + loan.getAmount() + " credited to account", Notification.NotificationType.INFO);
    }

    public void rejectLoan(Loan loan) {
        loan.setStatus(LoanStatus.REJECTED);
    }

    // Installment payment
    public void payInstallment(Loan loan, Account account, Customer customer, double amount) {
        if (loan.getStatus() != LoanStatus.APPROVED) {
            throw new IllegalStateException("Cannot pay. Loan not approved yet.");
        }

        if (account.isClosed()) {
            throw new IllegalStateException("Cannot pay from a closed account.");
        }

        if (account.isFrozen()) {
            throw new IllegalStateException("Cannot pay from a frozen account.");
        }

        double installment = loan.getMonthlyInstallment();

        // Late penalty
        long currentTime = System.currentTimeMillis();
        if (currentTime > loan.getNextDueDate()) {
            double penalty = installment * 0.05; // 5%
            installment += penalty;
            System.out.println("Late payemnt! Penalty applied: " + penalty);
        }

        if (!account.canWithdraw(installment)) {
            System.out.println("Insufficient balance for installment");
            return;
        }

        account.decreaseBalance(installment);
        loan.setRemainingBalance(loan.getRemainingBalance() - installment);
        loan.setMonthsPaid(loan.getMonthsPaid() + 1);

        Transaction transaction = new Transaction(amount, TransactionType.LOAN_INSTALLMENT);
        account.addTransaction(transaction);

        System.out.println("Loan Payment Done. Amount:" + installment);

        // Set next due date
        loan.setNextDueDate(currentTime + (30L * 24 * 60 * 60 * 1000));

        customer.notifyUser("Loan installment paid: " + installment, Notification.NotificationType.INFO);

        if (loan.getRemainingBalance() <= 0) {
            loan.setStatus(LoanStatus.COMPLETED);
            customer.notifyUser("Loan fully paid!", Notification.NotificationType.INFO);
        }
    }

    // Due dates
    public void checkDueLoans(List<User> users) {
        long now = System.currentTimeMillis();

        for (User user : users) {
            if (user instanceof Customer) {
                Customer c = (Customer) user;

                for (Loan loan : c.getLoans()) {
                    if (loan.getStatus() == LoanStatus.APPROVED) {
                        long diff = loan.getNextDueDate() - now;

                        // 3 days before due
                        if (diff <= (3L * 24 * 60 * 60 * 1000) && diff > 0) {
                            c.notifyUser("Loan installment due soon!", Notification.NotificationType.ALERT);
                        }

                        //overdue
                        if (now > loan.getNextDueDate()) {
                            c.notifyUser("Loan payment overdue!", Notification.NotificationType.WARNING);
                        }
                    }
                }
            }
        }
    }
}

/*
 DESIGN DECISIONS & OOP PRINCIPLES:

 1. SINGLE RESPONSIBILITY:
    - Handles all loan-related operations (approval, rejection, repayment).

 2. SEPARATION OF CONCERNS:
    - Business logic is separated from controller and model.

 3. ABSTRACTION:
    - Complex operations like EMI handling and penalties are hidden internally.

 4. REAL-WORLD MODELING:
    - Includes:
        - Loan approval flow
        - Installments
        - Late payment penalties
        - Due date tracking

 5. ENCAPSULATION:
    - Uses getters/setters instead of direct field access.

 6. EXTENSIBILITY:
    - Can support different loan types, interest models, and penalties.

 7. EVENT-BASED NOTIFICATIONS:
    - Notifies users about approvals, payments, and completion.
*/