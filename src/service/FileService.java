package service;

import model.User;
import util.FileHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * FileService handles data persistence using Java Serialization.
 *
 * DESIGN DECISION:
 * We use serialization because:
 * - It allows storing complete object structures (Users --> Accounts --> Transactions)
 * - It is simple and suitable for small-scale applications like this project
 * - It fulfills the assignment requirements of file handling
 */

public class FileService {
    // File path to store user data
    private static final String FILE_PATH = "bank_data.ser";

    private FileHandler fileHandler = new FileHandler();

    // Save all users to file
    public void saveUsers(List<User> users) {
        fileHandler.writeObject(users, FILE_PATH);
        System.out.println("Data saved successfully.");
    }

    // Load users from file
    public List<User> loadUsers() {
        File file = new File(FILE_PATH);

        // If file doesn't exist, return empty list
        if (!file.exists()) {
            return new ArrayList<>();
        }

        Object obj = fileHandler.readObject(FILE_PATH);

        if (obj != null) {
            return (List<User>) obj;
        }

        return new ArrayList<>();
    }
}
