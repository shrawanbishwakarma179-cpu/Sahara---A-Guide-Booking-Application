package com.sahara.dao;

import com.sahara.model.Booking;
import com.sahara.model.Tour;
import com.sahara.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** BookingDAO — handles all booking operations including Booking_Tour junction. */
public class BookingDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Create a booking and link selected tours.
     * Uses a Queue concept (FCFS) — bookings are inserted in order received.
     */
    public boolean createBooking(Booking booking, List<String> tourIds) {
        String sql = "INSERT INTO Booking (TouristID, GuideID, BookingDate, PaymentStatus, Status) VALUES (?,?,?,?,?)";
        try {
            Connection conn = getConn();
            conn.setAutoCommit(false); // Transaction

            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, booking.getTouristId());
            ps.setInt(2, booking.getGuideId());
            ps.setDate(3, Date.valueOf(booking.getBookingDate()));
            ps.setString(4, booking.getPaymentStatus());
            ps.setString(5, "Pending");
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (!keys.next()) { conn.rollback(); return false; }
            int bookingId = keys.getInt(1);

            // Insert into Booking_Tour junction
            String btSql = "INSERT INTO Booking_Tour (BookingID, TourID) VALUES (?,?)";
            for (String tourId : tourIds) {
                PreparedStatement btPs = conn.prepareStatement(btSql);
                btPs.setInt(1, bookingId);
                btPs.setString(2, tourId);
                btPs.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            System.err.println("BookingDAO.create: " + e.getMessage());
            try { getConn().rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            return false;
        }
    }

    /** Get all bookings (admin view). */
    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, t.TouristName, g.GuideName " +
                     "FROM Booking b " +
                     "JOIN Tourist t ON b.TouristID=t.TouristID " +
                     "JOIN Guide g ON b.GuideID=g.GuideID " +
                     "ORDER BY b.BookingDate DESC";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("BookingDAO.getAll: " + e.getMessage());
        }
        return list;
    }

    /** Get bookings for a specific tourist. */
    public List<Booking> getByTourist(int touristId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, t.TouristName, g.GuideName " +
                     "FROM Booking b " +
                     "JOIN Tourist t ON b.TouristID=t.TouristID " +
                     "JOIN Guide g ON b.GuideID=g.GuideID " +
                     "WHERE b.TouristID=? ORDER BY b.BookingDate DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, touristId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("BookingDAO.getByTourist: " + e.getMessage());
        }
        return list;
    }

    /** Get bookings for a specific guide. */
    public List<Booking> getByGuide(int guideId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, t.TouristName, g.GuideName " +
                     "FROM Booking b " +
                     "JOIN Tourist t ON b.TouristID=t.TouristID " +
                     "JOIN Guide g ON b.GuideID=g.GuideID " +
                     "WHERE b.GuideID=? ORDER BY b.BookingDate DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, guideId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("BookingDAO.getByGuide: " + e.getMessage());
        }
        return list;
    }

    /** Update booking status (Approve / Reject by admin). */
    public boolean updateStatus(int bookingId, String status, int adminId) {
        String sql = "UPDATE Booking SET Status=?, AdminID=? WHERE BookingID=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, adminId);
            ps.setInt(3, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("BookingDAO.updateStatus: " + e.getMessage());
            return false;
        }
    }

    /** Update payment status. */
    public boolean updatePaymentStatus(int bookingId, String paymentStatus) {
        String sql = "UPDATE Booking SET PaymentStatus=? WHERE BookingID=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, paymentStatus);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("BookingDAO.updatePayment: " + e.getMessage());
            return false;
        }
    }

    /** Get tours linked to a booking. */
    public List<Tour> getToursByBooking(int bookingId) {
        List<Tour> tours = new ArrayList<>();
        String sql = "SELECT t.* FROM Tour t JOIN Booking_Tour bt ON t.TourID=bt.TourID WHERE bt.BookingID=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tours.add(new Tour(
                    rs.getString("TourID"),
                    rs.getString("TourName"),
                    rs.getString("Duration"),
                    rs.getDouble("Price")
                ));
            }
        } catch (SQLException e) {
            System.err.println("BookingDAO.getTours: " + e.getMessage());
        }
        return tours;
    }

    /** Count of total bookings (for admin dashboard). */
    public int getTotalBookings() {
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Booking")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Booking mapRow(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setBookingId(rs.getInt("BookingID"));
        b.setTouristId(rs.getInt("TouristID"));
        b.setGuideId(rs.getInt("GuideID"));
        b.setBookingDate(rs.getDate("BookingDate").toLocalDate());
        b.setPaymentStatus(rs.getString("PaymentStatus"));
        b.setStatus(rs.getString("Status"));
        b.setTouristName(rs.getString("TouristName"));
        b.setGuideName(rs.getString("GuideName"));
        return b;
    }
}
