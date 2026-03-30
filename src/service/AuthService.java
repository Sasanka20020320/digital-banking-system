package service;

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
