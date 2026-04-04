package service;

import model.Admin;
import model.User;
import java.util.List;

public class AuthService {
    public User login(String email, String password, List<User> users) {
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
}

/*
 DESIGN DECISIONS & OOP PRINCIPLES:

 1. SINGLE RESPONSIBILITY:
    - Handles only authentication (login functionality).

 2. SEPARATION OF CONCERNS:
    - Authentication logic is separated from controller and model layers.

 3. ABSTRACTION:
    - Controller does not need to know how login works internally.

 4. LOOSE COUPLING:
    - Works with generic User objects instead of specific roles (Customer, Staff, Admin).

 5. EXTENSIBILITY:
    - Can be extended to support hashing, JWT, or database authentication.

 NOTE:
    - Current implementation uses plain text password comparison (suitable for academic use).
*/