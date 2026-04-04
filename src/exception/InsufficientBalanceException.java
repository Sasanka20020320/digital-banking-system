package exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}

/*
==================== DESIGN DECISIONS - EXCEPTION LAYER ====================

1. CUSTOM EXCEPTIONS
   - Custom exceptions are created to represent domain-specific errors:
        - InsufficientBalanceException → insufficient funds
        - InvalidAccountException → invalid account reference
        - InvalidAmountException → invalid monetary input

2. EXTENDS RUNTIMEEXCEPTION
   - All exceptions extend RuntimeException (unchecked exceptions)
   - This avoids mandatory try-catch blocks and keeps code clean
   - Suitable for this application where errors indicate invalid operations

3. SEPARATION OF CONCERNS
   - Error handling is separated from business logic
   - Improves readability and maintainability

4. MEANINGFUL ERROR HANDLING
   - Provides clear and specific error messages
   - Helps debugging and improves user feedback

5. SCALABILITY
   - Easy to extend by adding new domain-specific exceptions
   - Example: FraudException, UnauthorizedAccessException

===========================================================================
*/