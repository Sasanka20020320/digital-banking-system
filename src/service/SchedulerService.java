package service;

import model.*;

import java.time.LocalDate;
import java.util.List;

public class SchedulerService {
    private long lastInterestApplied = 0;
    private long lastLoanCheck = 0;

    public static final long ONE_DAY = 24L * 60 * 60 * 1000;
    public static final long ONE_MONTH = 30L * 24 * 60 * 60 * 1000;

    // Run scheduled tasks
    public void run(List<User> users) {
        long now = System.currentTimeMillis();

        // Monthly Interest
        if (now - lastInterestApplied >= ONE_MONTH) {
            applyMonthlyInterest(users);
            generateMonthlyStatement(users);
            lastInterestApplied = now;
            System.out.println("Monthly interest applied.");
        }

        // Daily Loan check
        if (now - lastLoanCheck >= ONE_DAY) {
            checkLoanReminders(users);
            checkBillReminders(users);
            lastLoanCheck = now;
        }
    }

    // Apply interest to savings
    public void applyMonthlyInterest(List<User> users) {
        for (User user : users) {
            if (user instanceof Customer) {
                Customer c = (Customer) user;

                for (Account acc : c.getAccounts()) {
                    if (acc instanceof SavingsAccount) {
                        ((SavingsAccount) acc). addInterest();
                    }

                    if (acc instanceof FixedDepositAccount) {
                        FixedDepositAccount fd = (FixedDepositAccount) acc;
                        if (fd.isMatured()) {
                            fd.applyMaturityInterest();
                        }
                    }
                }
            }
        }
    }

    // Loan reminder
    private void checkLoanReminders(List<User> users) {
        LoanService loanService = new LoanService();
        loanService.checkDueLoans(users);
    }

    // Bill reminder
    private void checkBillReminders(List<User> users) {
        for (User user : users) {
            if (user instanceof Customer) {
                Customer c = (Customer) user;

                for (BillPayment bill : c.getBillPayments()) {
                    if (bill.isDueSoon()) {
                        c.notifyUser("Bill Payment due soon: " + bill.getBillType(), Notification.NotificationType.WARNING);
                    }
                }
            }
        }
    }

    // Automatic Monthly Statement Generation
    public void generateMonthlyStatement(List<User> users) {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        for (User user : users) {
            if (user instanceof Customer customer) {
                for (Account acc : customer.getAccounts()) {
                    String statement = acc.generateMonthlyStatement(month, year);
                    customer.notifyUser("Your monthly statement for account " + acc.getAccountNumber() +
                            " is ready.", Notification.NotificationType.INFO);
                }
            }
        }
    }
}
