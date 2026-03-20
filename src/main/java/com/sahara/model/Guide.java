package com.sahara.model;

/** Guide — inherits User, represents a professional tour guide. */
public class Guide extends User {
    private String phone;
    private String availabilityStatus;
    private double rating;

    public Guide() {}

    public Guide(int id, String name, String email, String phone,
                 String availabilityStatus, double rating, String password) {
        super(id, name, email, password);
        this.phone = phone;
        this.availabilityStatus = availabilityStatus;
        this.rating = rating;
    }

    public String getPhone()                     { return phone; }
    public void setPhone(String ph)              { this.phone = ph; }

    public String getAvailabilityStatus()        { return availabilityStatus; }
    public void setAvailabilityStatus(String s)  { this.availabilityStatus = s; }

    public double getRating()                    { return rating; }
    public void setRating(double r)              { this.rating = r; }

    @Override
    public String getRole()                      { return "Guide"; }

    @Override
    public String toString()                     { return name + " (Rating: " + rating + ")"; }
}
