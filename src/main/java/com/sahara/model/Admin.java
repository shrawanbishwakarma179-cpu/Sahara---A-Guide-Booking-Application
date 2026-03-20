package com.sahara.model;

/** Admin — inherits User, manages the entire system. */
public class Admin extends User {

    public Admin() {}

    public Admin(int id, String name, String email, String password) {
        super(id, name, email, password);
    }

    @Override
    public String getRole() { return "Admin"; }
}
