package com.sahara.model;

/** Tourist — inherits User, represents a tourist who books tours. */
public class Tourist extends User {
    private String phone;

    public Tourist() {}

    public Tourist(int id, String name, String email, String phone, String password) {
        super(id, name, email, password);
        this.phone = phone;
    }

    public String getPhone()         { return phone; }
    public void setPhone(String ph)  { this.phone = ph; }

    @Override
    public String getRole()          { return "Tourist"; }
}
