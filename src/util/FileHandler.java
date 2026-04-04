package util;

import java.io.*;

public class FileHandler {
    // Write any object to file
    public void writeObject(Object obj, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    // Read any object from file
    public Object readObject(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return null;
    }
}

/*
==================== DESIGN DECISIONS - FILE HANDLER ====================

1. UTILITY CLASS PURPOSE
   - FileHandler is a reusable utility class for handling file operations.
   - Provides generic methods for reading and writing objects.

2. SERIALIZATION USAGE
   - Uses Java Object Serialization (ObjectOutputStream / ObjectInputStream)
   - Allows storing complete object graphs:
        Users → Accounts → Transactions

3. GENERIC DESIGN
   - Methods accept Object type:
        writeObject(Object obj, String filePath)
        readObject(String filePath)
   - Makes the class reusable for any serializable object.

4. ABSTRACTION
   - Hides low-level file handling details from service layer.
   - Service layer (FileService) interacts with this class instead of raw I/O.

5. EXCEPTION HANDLING
   - Uses try-with-resources to ensure automatic resource closing.
   - Catches IO and ClassNotFound exceptions to prevent crashes.

6. SEPARATION OF CONCERNS
   - File handling logic is isolated from business logic.
   - Keeps service and controller layers clean.

7. DESIGN TRADE-OFF
   - Returns Object → requires casting in service layer.
   - Simpler approach suitable for small-scale systems.

========================================================================
*/