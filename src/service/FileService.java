package service;

import model.User;

import java.io.*;
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

    // Save all users to file
    public void saveUsers(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            //Write entire user list to file
            oos.writeObject(users);

            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    // Load users from file
    public List<User> loadUsers() {
        File file = new File(FILE_PATH);

        // If file doesn't exist, return empty list
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            // Read object and cast to List<User>
            return (List<User>) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }

        return new ArrayList<>();
    }
}
