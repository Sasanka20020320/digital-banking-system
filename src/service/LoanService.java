package service;

import model.Loan;
import model.LoanStatus;
import model.Account;
import model.Customer;

public class LoanService {
    public void approveLoan(Loan loan, Account account, Customer customer) {
        loan.setStatus(LoanStatus.APPROVED);
        account.deposit(loan.getAmount());

        customer.notifyUser("Loan approved: " + loan.getAmount() + " credited to account");
    }

    public void rejectLoan(Loan loan) {
        loan.setStatus(LoanStatus.REJECTED);
    }
}
