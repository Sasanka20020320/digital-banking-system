package util;

public class ValidationUtil {

    public static boolean isValidAmount(double amount) {
        return amount > 0;
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.contains("@");
    }
}

/*
==================== DESIGN DECISIONS - VALIDATION UTIL ====================

1. PURPOSE
   - Reserved utility class for input validation logic.
   - Centralizes validation rules (email, password, amount, etc.).

2. SEPARATION OF CONCERNS
   - Keeps validation logic separate from controller and service layers.
   - Prevents duplication of validation code.

3. REUSABILITY
   - Validation methods can be reused across the system.
   - Example future methods:
        - isValidEmail()
        - isStrongPassword()
        - isValidAmount()

4. EXTENSIBILITY
   - Designed for future enhancements without modifying existing classes.

5. CLEAN ARCHITECTURE PRACTICE
   - Helps maintain clean and organized codebase.
   - Encourages modular design.

========================================================================
*/