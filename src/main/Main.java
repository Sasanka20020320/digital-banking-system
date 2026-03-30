package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import controller.BankController;
import exception.InsufficientBalanceException;
import exception.InvalidAccountException;
import exception.InvalidAmountException;
import model.*;
import service.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        List<User> users = new ArrayList<>();

        Customer customer = new Customer(1, "Sasanka", "sasanka@email.com", "1234");
        Staff staff = new Staff(2, "Wishwa", "wishwa@email.com", "1234");

        users.add(customer);
        users.add(staff);

        // Add accounts
        Account acc1 = new SavingsAccount(101, 5000, 0.1);
        Account acc2 = new StudentAccount(102, 3000);
        Account acc3 = new CheckingAccount(103, 1000, -1000);
        Account acc4 = new FixedDepositAccount(104, 10000, 0.2, 12);

        customer.addAccount(acc1);
        customer.addAccount(acc2);
        customer.addAccount(acc3);
        customer.addAccount(acc4);

        // Services
        TransactionService ts = new TransactionService();
        LoanService loanService = new LoanService();
        AuthService authService = new AuthService();

        // Controller
        BankController controller = new BankController(authService, ts, loanService);
        controller.login(users, scanner);
    }
}