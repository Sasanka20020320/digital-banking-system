package service;

import model.*;
import exception.*;

/*
Separates business logic from controller, following layered architecture.
 */

public class BillPaymentService {
    // Pay a bill from a given account
    public void payBill(Account account, String billType, double amount, Customer customer) {
        if (account == null) {
            throw new InvalidAccountException("Invalid account");
        }

        if (amount <= 0) {
            throw new InvalidAmountException("Bill amount must be positive");
        }

        try {
            // Withdraw money from account
            account.withdraw(amount, TransactionType.BILL_PAYMENT);

            // Create bill record
            BillPayment bill = new BillPayment(billType, amount);

            // Store bill in customer
            customer.payBill(bill);

            // Store as a transaction
//            account.addTransaction(new Transaction(amount, TransactionType.BILL_PAYMENT));

            // Notify user
            customer.notifyUser("Bill paid: " + billType + " amount " + amount, Notification.NotificationType.INFO);

            System.out.println("Bill payment successful");
        } catch (Exception e) {
            customer.notifyUser("Bill payment failed: " + e.getMessage(), Notification.NotificationType.ALERT);
            throw e;
        }
    }
}

/*
 DESIGN DECISIONS & OOP PRINCIPLES:

 1. SEPARATION OF CONCERNS:
    - Handles business logic of bill payments instead of placing it in controller.

 2. SINGLE RESPONSIBILITY:
    - Responsible only for bill-related operations.

 3. ENCAPSULATION:
    - Uses Account and Customer methods instead of modifying data directly.

 4. EXCEPTION HANDLING:
    - Validates account and amount before processing.

 5. REAL-WORLD MODELING:
    - Simulates bill deduction, record creation, and notification.

 6. LOOSE COUPLING:
    - Depends on model classes but does not control their internal implementation.

 7. EXTENSIBILITY:
    - Can be extended for bill categories, auto-pay, or external APIs.
*/