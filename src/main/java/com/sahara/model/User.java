package com.sahara.model;

/**
 * Abstract User class — Base class demonstrating Inheritance.
 * Tourist, Guide and Admin all extend this class.
 */
public abstract class User {
    protected int id;
    protected String name;
    protected String email;
    protected String password;

    public User() {}

    public User(int id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Encapsulation — private fields exposed via getters/setters
    public int getId()               { return id; }
    public void setId(int id)        { this.id = id; }

    public String getName()          { return name; }
    public void setName(String n)    { this.name = n; }

    public String getEmail()         { return email; }
    public void setEmail(String e)   { this.email = e; }

    public String getPassword()      { return password; }
    public void setPassword(String p){ this.password = p; }

    /** Polymorphic method — each subclass provides its role label. */
    public abstract String getRole();

    @Override
    public String toString() {
        return "[" + getRole() + "] " + name + " (" + email + ")";
    }
}
