package model;

import java.io.Serializable;
import service.LoanService;

public class Staff extends User implements Serializable {
    // Prevent Serialization errors
    private static final long serialVersionUID = 1L;

    public Staff(int id, String name, String email, String password) {
        super(id, name, email, password);
    }

    public void approveLoan(Loan loan, Account account, Customer customer, LoanService loanService) {
        loanService.approveLoan(loan, account, customer);
    }

    public void rejectLoan(Loan loan, LoanService loanService) {
        loanService.rejectLoan(loan);
    }
}
