//package main;
//
//import java.util.List;
//
//import controller.BankController;
//import model.*;
//import service.*;
//
//public class Main {
//    public static void main(String[] args) {
//        FileService fileService = new FileService();
//
//        // Load users from file
//        List<User> users = fileService.loadUsers();
//
//        // If file is empty --> create default data
//        if (users.isEmpty()) {
//            Customer customer = new Customer(1, "Sasanka", "sasanka@email.com", "1234");
//            Staff staff = new Staff(2, "Wishwa", "wishwa@email.com", "1234");
//            Admin admin = new Admin(3, "Admin", "admin@email.com", "1234");
//
//            users.add(customer);
//            users.add(staff);
//            users.add(admin);
//
//            // Add accounts
//            Account acc1 = new SavingsAccount(101, 5000, 0.1);
//            Account acc2 = new StudentAccount(102, 3000);
//            Account acc3 = new CheckingAccount(103, 1000, -1000);
//            Account acc4 = new FixedDepositAccount(104, 10000, 0.2, 12);
//
//            customer.addAccount(acc1);
//            customer.addAccount(acc2);
//            customer.addAccount(acc3);
//            customer.addAccount(acc4);
//
//            // Save initial data
//            fileService.saveUsers(users);
//        }
//
//        // Services
//        TransactionService ts = new TransactionService();
//        LoanService loanService = new LoanService();
//        AuthService authService = new AuthService();
//        BillPaymentService billService = new BillPaymentService();
//        SchedulerService schedular = new SchedulerService();
//
//        // Controller
//        BankController controller = new BankController(authService, ts, loanService, billService);
//
//        // Main menu
//        boolean exit = false;
//
//        schedular.run(users);
//
//        while(!exit) {
//
//            System.out.println("=== MAIN MENU ===");
//            System.out.println("1. Register");
//            System.out.println("2. Login");
//            System.out.println("3. Exit");
//            System.out.println("Choose option: ");
//
//            int mainChoice = scanner.nextInt();
//
//            switch (mainChoice) {
//                case 1:
//                    controller.register(users, scanner);
//                    fileService.saveUsers(users);
//                    break;
//                case 2:
//                    try {
//                        controller.login(users, scanner);
//                    } catch (Exception e) {
//                        System.out.println("Error: " + e.getMessage());
//                    }
//                    break;
//                case 3:
//                    exit = true;
//                    break;
//                default:
//                    System.out.println("Invalid choice");
//            }
//        }
//
//        // Save before exiting
//        fileService.saveUsers(users);
//    }
//}
//
///*
//==================== DESIGN DECISIONS - MAIN LAYER ====================
//
//1. ENTRY POINT (APPLICATION START)
//   - Main class acts as the entry point of the system.
//   - Responsible for initializing the application and starting the program flow.
//
//2. LAYERED ARCHITECTURE COORDINATION
//   - Main connects all layers together:
//        - Model → Data structures (User, Account, etc.)
//        - Service → Business logic (TransactionService, LoanService, etc.)
//        - Controller → User interaction (BankController)
//   - Ensures proper separation of concerns.
//
//3. DEPENDENCY INITIALIZATION
//   - All service objects are created here and injected into the controller.
//   - Follows Dependency Injection principle to reduce tight coupling.
//
//4. DATA PERSISTENCE (FILE HANDLING)
//   - Uses FileService to:
//        - Load existing users from file at startup
//        - Save data before exit
//   - Ensures data is not lost between program runs.
//
//5. DEFAULT DATA INITIALIZATION
//   - If no saved data exists:
//        - Creates default users (Customer, Staff, Admin)
//        - Creates sample accounts
//   - Helps in testing and demonstration without manual setup.
//
//6. SCHEDULER EXECUTION
//   - SchedulerService is triggered at the start:
//        - Applies monthly interest
//        - Generates statements
//        - Checks loan and bill reminders
//   - Simulates real-world background banking operations.
//
//7. USER INTERACTION LOOP
//   - Implements a loop-based menu system:
//        - Register
//        - Login
//        - Exit
//   - Keeps application running until user chooses to exit.
//
//8. EXCEPTION HANDLING
//   - Basic try-catch used during login to prevent system crash.
//   - Improves robustness and user experience.
//
//9. SCALABILITY & EXTENSIBILITY
//   - New services or features can be added easily by:
//        - Initializing them here
//        - Injecting into controller
//   - No need to modify core flow significantly.
//
//10. DESIGN TRADE-OFFS
//   - Uses simple console-based UI (Scanner) for simplicity.
//   - Scheduler runs manually instead of background threads.
//   - Suitable for academic/small-scale system.
//
//=======================================================================
//*/