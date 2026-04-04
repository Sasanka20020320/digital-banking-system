package service;

import model.User;
import util.FileHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

/*
 DESIGN DECISIONS & OOP PRINCIPLES:

 1. SEPARATION OF CONCERNS:
    - Handles only file persistence logic.

 2. SINGLE RESPONSIBILITY:
    - Responsible for saving and loading user data.

 3. ABSTRACTION:
    - Hides serialization logic from the rest of the system.

 4. LOOSE COUPLING:
    - Uses FileHandler utility instead of directly handling file streams.

 5. DATA PERSISTENCE STRATEGY:
    - Uses Java Serialization to store complete object graphs
      (Users → Accounts → Transactions).

 6. EXTENSIBILITY:
    - Can be replaced with database (JDBC, MySQL) without affecting other layers.

 NOTE:
    - Suitable for small-scale systems and academic projects.
*/