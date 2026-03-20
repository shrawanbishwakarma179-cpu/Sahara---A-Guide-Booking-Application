package com.sahara;

import com.sahara.util.DatabaseConnection;
import com.sahara.dao.*;
import com.sahara.model.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * DBConnectionTest — standalone test class.
 *
 * Run this class FIRST to verify your database connection works
 * before launching the full JavaFX application.
 *
 * How to run in Eclipse:
 *   Right-click DBConnectionTest.java -> Run As -> Java Application
 */
public class DBConnectionTest {

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("  SAHARA — Database Connectivity Test");
        System.out.println("==============================================");

        // ── Test 1: Basic Connection ──────────────────────────────────────
        System.out.println("\n[TEST 1] Connecting to database...");
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        Connection conn = dbConn.getConnection();

        if (conn == null) {
            System.err.println("FAILED: Connection is null.");
            System.err.println("  -> Check: Is MySQL running?");
            System.err.println("  -> Check: Did you run sql/sahara_schema.sql?");
            System.err.println("  -> Check: Is the password correct in db.properties?");
            return;
        }

        if (dbConn.isConnected()) {
            System.out.println("PASSED: " + dbConn.getConnectionInfo());
        } else {
            System.err.println("FAILED: isConnected() returned false.");
            return;
        }

        // ── Test 2: Query sahara_db tables ────────────────────────────────
        System.out.println("\n[TEST 2] Listing tables in sahara_db...");
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SHOW TABLES")) {
            int tableCount = 0;
            while (rs.next()) {
                System.out.println("  Table: " + rs.getString(1));
                tableCount++;
            }
            if (tableCount == 0) {
                System.err.println("WARNING: No tables found. Did you run sql/sahara_schema.sql?");
            } else {
                System.out.println("PASSED: Found " + tableCount + " tables.");
            }
        } catch (Exception e) {
            System.err.println("FAILED: " + e.getMessage());
        }

        // ── Test 3: TouristDAO ────────────────────────────────────────────
        System.out.println("\n[TEST 3] TouristDAO.getAllTourists()...");
        try {
            TouristDAO touristDAO = new TouristDAO();
            List<Tourist> tourists = touristDAO.getAllTourists();
            System.out.println("PASSED: Found " + tourists.size() + " tourist(s).");
            tourists.forEach(t -> System.out.println("  -> " + t.getName() + " | " + t.getEmail()));
        } catch (Exception e) {
            System.err.println("FAILED: " + e.getMessage());
        }

        // ── Test 4: GuideDAO ──────────────────────────────────────────────
        System.out.println("\n[TEST 4] GuideDAO.getAllGuides()...");
        try {
            GuideDAO guideDAO = new GuideDAO();
            List<Guide> guides = guideDAO.getAllGuides();
            System.out.println("PASSED: Found " + guides.size() + " guide(s).");
            guides.forEach(g -> System.out.println("  -> " + g.getName() + " | Rating: " + g.getRating() + " | " + g.getAvailabilityStatus()));
        } catch (Exception e) {
            System.err.println("FAILED: " + e.getMessage());
        }

        // ── Test 5: TourDAO ───────────────────────────────────────────────
        System.out.println("\n[TEST 5] TourDAO.getAllTours()...");
        try {
            TourDAO tourDAO = new TourDAO();
            List<Tour> tours = tourDAO.getAllTours();
            System.out.println("PASSED: Found " + tours.size() + " tour(s).");
            tours.forEach(t -> System.out.println("  -> [" + t.getTourId() + "] " + t.getTourName() + " | $" + t.getPrice()));
        } catch (Exception e) {
            System.err.println("FAILED: " + e.getMessage());
        }

        // ── Test 6: AdminDAO login ────────────────────────────────────────
        System.out.println("\n[TEST 6] AdminDAO.login(admin@sahara.com, admin123)...");
        try {
            AdminDAO adminDAO = new AdminDAO();
            Admin admin = adminDAO.login("admin@sahara.com", "admin123");
            if (admin != null) {
                System.out.println("PASSED: Admin logged in -> " + admin.getName() + " | " + admin.getEmail());
            } else {
                System.out.println("WARNING: Admin login returned null. Check seed data.");
            }
        } catch (Exception e) {
            System.err.println("FAILED: " + e.getMessage());
        }

        // ── Test 7: Tourist login ─────────────────────────────────────────
        System.out.println("\n[TEST 7] TouristDAO.login(john@example.com, tourist123)...");
        try {
            TouristDAO touristDAO = new TouristDAO();
            Tourist tourist = touristDAO.login("john@example.com", "tourist123");
            if (tourist != null) {
                System.out.println("PASSED: Tourist logged in -> " + tourist.getName());
            } else {
                System.out.println("WARNING: Tourist login null. Check seed data.");
            }
        } catch (Exception e) {
            System.err.println("FAILED: " + e.getMessage());
        }

        // ── Test 8: BookingDAO ────────────────────────────────────────────
        System.out.println("\n[TEST 8] BookingDAO.getAllBookings()...");
        try {
            BookingDAO bookingDAO = new BookingDAO();
            List<Booking> bookings = bookingDAO.getAllBookings();
            System.out.println("PASSED: Found " + bookings.size() + " booking(s).");
        } catch (Exception e) {
            System.err.println("FAILED: " + e.getMessage());
        }

        // ── Summary ───────────────────────────────────────────────────────
        System.out.println("\n==============================================");
        System.out.println("  All tests complete.");
        System.out.println("  If all PASSed -> Run MainApp.java to start SAHARA.");
        System.out.println("==============================================");

        dbConn.closeConnection();
    }
}
