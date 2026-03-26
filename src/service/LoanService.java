package service;

import model.Loan;
import model.LoanStatus;
import model.Account;

public class LoanService {
    public void approveLoan(Loan loan, Account account) {
        loan.setStatus(LoanStatus.APPROVED);
        account.deposit(loan.getAmount());
    }

    public void rejectLoan(Loan loan) {
        loan.setStatus(LoanStatus.REJECTED);
    }
}
