package com.sahara.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Booking — stores booking information linking Tourist, Guide, and Tours.
 * Uses LocalDate for booking date to support date comparisons and filtering.
 */
public class Booking {

    // ----- Core booking fields -----
    private int bookingId;          // Unique booking ID
    private int touristId;          // FK to Tourist
    private int guideId;            // FK to Guide
    private int adminId;            // FK to Admin
    private LocalDate bookingDate;  // Booking date, type-safe
    private String paymentStatus;   // e.g., Paid, Pending
    private String status;          // e.g., Pending, Confirmed, Cancelled

    // ----- Denormalized display fields (populated via JOIN in DAO) -----
    private String touristName;     // Display name of tourist
    private String guideName;       // Display name of guide
    private List<Tour> tours = new ArrayList<>(); // List of tours linked to this booking

    // ----- Constructors -----
    public Booking() {}

    public Booking(int touristId, int guideId, LocalDate bookingDate, String paymentStatus) {
        this.touristId = touristId;
        this.guideId = guideId;
        this.bookingDate = bookingDate;
        this.paymentStatus = paymentStatus;
        this.status = "Pending"; // default status
    }

    // ----- Getters and Setters -----
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public int getTouristId() { return touristId; }
    public void setTouristId(int touristId) { this.touristId = touristId; }

    public int getGuideId() { return guideId; }
    public void setGuideId(int guideId) { this.guideId = guideId; }

    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }

    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTouristName() { return touristName; }
    public void setTouristName(String touristName) { this.touristName = touristName; }

    public String getGuideName() { return guideName; }
    public void setGuideName(String guideName) { this.guideName = guideName; }

    public List<Tour> getTours() { return tours; }
    public void setTours(List<Tour> tours) { this.tours = tours; }

    // ----- Utility Methods -----
    /**
     * Returns true if the booking is for today's date.
     */
    public boolean isToday() {
        return bookingDate != null && bookingDate.isEqual(LocalDate.now());
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", touristName='" + touristName + '\'' +
                ", guideName='" + guideName + '\'' +
                ", bookingDate=" + bookingDate +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}