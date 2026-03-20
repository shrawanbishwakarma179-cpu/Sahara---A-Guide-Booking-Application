package com.sahara.dao;

import com.sahara.model.Guide;
import com.sahara.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** GuideDAO — handles all CRUD operations for Guide in the DB. */
public class GuideDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /** Register a new guide. */
    public boolean register(Guide guide) {
        String sql = "INSERT INTO Guide (GuideName, GuideEmail, GuidePhone, AvailabilityStatus, Rating, Password) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, guide.getName());
            ps.setString(2, guide.getEmail());
            ps.setString(3, guide.getPhone());
            ps.setString(4, guide.getAvailabilityStatus());
            ps.setDouble(5, guide.getRating());
            ps.setString(6, guide.getPassword());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("GuideDAO.register: " + e.getMessage());
            return false;
        }
    }

    /** Authenticate guide by email and password. */
    public Guide login(String email, String password) {
        String sql = "SELECT * FROM Guide WHERE GuideEmail=? AND Password=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("GuideDAO.login: " + e.getMessage());
        }
        return null;
    }

    /** Get all guides. */
    public List<Guide> getAllGuides() {
        List<Guide> list = new ArrayList<>();
        String sql = "SELECT * FROM Guide";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("GuideDAO.getAll: " + e.getMessage());
        }
        return list;
    }

    /** Get only available guides. */
    public List<Guide> getAvailableGuides() {
        List<Guide> list = new ArrayList<>();
        String sql = "SELECT * FROM Guide WHERE AvailabilityStatus='Available'";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("GuideDAO.getAvailable: " + e.getMessage());
        }
        return list;
    }

    /** Get guide by ID. */
    public Guide getById(int id) {
        String sql = "SELECT * FROM Guide WHERE GuideID=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("GuideDAO.getById: " + e.getMessage());
        }
        return null;
    }

    /** Update guide profile. */
    public boolean update(Guide guide) {
        String sql = "UPDATE Guide SET GuideName=?, GuidePhone=?, AvailabilityStatus=? WHERE GuideID=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, guide.getName());
            ps.setString(2, guide.getPhone());
            ps.setString(3, guide.getAvailabilityStatus());
            ps.setInt(4, guide.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("GuideDAO.update: " + e.getMessage());
            return false;
        }
    }

    /** Update guide rating (called after feedback submission). */
    public boolean updateRating(int guideId, double newRating) {
        String sql = "UPDATE Guide SET Rating=? WHERE GuideID=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setDouble(1, newRating);
            ps.setInt(2, guideId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("GuideDAO.updateRating: " + e.getMessage());
            return false;
        }
    }

    /** Delete guide by ID. */
    public boolean delete(int id) {
        String sql = "DELETE FROM Guide WHERE GuideID=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("GuideDAO.delete: " + e.getMessage());
            return false;
        }
    }

    private Guide mapRow(ResultSet rs) throws SQLException {
        return new Guide(
            rs.getInt("GuideID"),
            rs.getString("GuideName"),
            rs.getString("GuideEmail"),
            rs.getString("GuidePhone"),
            rs.getString("AvailabilityStatus"),
            rs.getDouble("Rating"),
            rs.getString("Password")
        );
    }
}
