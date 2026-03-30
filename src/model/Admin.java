package model;

import java.io.Serializable;

public class Admin extends User implements Serializable {
    // Prevent Serialization errors
    private static final long serialVersionUID = 1L;

    public Admin(int id, String name, String email, String password) {


        super(id, name, email,password);
    }
}
