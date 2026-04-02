package service;

import model.*;

import java.time.Instant;
import java.util.List;

public class FraudDetectionService {
    private static final double LARGE_AMOUNT = 50000;

    public void analyzeTransaction(Account account, Transaction transaction) {
        // Large Transaction
        if (transaction.getAmount() > LARGE_AMOUNT) {
            transaction.setSuspicious(true);
        }

        // Too many transactions in short time
        int count = 0;
        Instant now = transaction.getTimestamp();

        for (Transaction t : account.getTransactions()) {
            long diff = Math.abs(now.toEpochMilli() - t.getTimestamp().toEpochMilli());

            if (diff <= 60000) {
                count++;
            }
        }

        if (count >= 3) {
            transaction.setSuspicious(true);
        }
    }

    // Get all suspicious transactions for a customer
    public void detectSuspiciousActivities(List<User> users) {
        for (User user : users) {
            if(user instanceof Customer c) {
                for (Account acc : c.getAccounts()) {
                    for (Transaction t : acc.getTransactions()) {
                        if (t.isSuspicious()) {
                            System.out.println("Suspicious Transaction Found: ");
                            System.out.println("Account: " + acc.getAccountNumber());
                            System.out.println(t);
                        }
                    }
                }
            }
        }
    }
}
