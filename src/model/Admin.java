package model;

import java.io.Serializable;

public class Admin extends User implements Serializable {
    // Prevent Serialization errors
    private static final long serialVersionUID = 1L;

    public Admin(int id, String name, String email, String password) {


        super(id, name, email,password);
    }
}

/*
 DESIGN DECISIONS & OOP PRINCIPLES:

 1. INHERITANCE:
    - Extends User.

 2. ROLE-BASED DESIGN:
    - Admin has higher privileges (system-wide operations).

 3. SINGLE RESPONSIBILITY:
    - Admin-specific actions handled in controller layer.

 4. EXTENSIBILITY:
    - Can be extended for audit logs, compliance, etc.
*/