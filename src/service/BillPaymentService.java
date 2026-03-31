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

        // Withdraw money from account
        account.decreaseBalance(amount);

        // Create bill record
        BillPayment bill = new BillPayment(billType, amount);

        // Store bill in customer
        customer.payBill(bill);

        // Store as a transaction
        account.addTransaction(new Transaction(amount, TransactionType.BILL_PAYMENT));

        // Notify user
        customer.notifyUser("Bill paid: " + billType + " amount " + amount);

        System.out.println("Bill payment successful");
    }
}
