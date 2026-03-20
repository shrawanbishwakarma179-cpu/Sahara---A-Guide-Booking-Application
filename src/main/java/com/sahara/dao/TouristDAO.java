package com.sahara.dao;

import com.sahara.model.Tourist;
import com.sahara.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** TouristDAO — handles all CRUD operations for Tourist in the DB. */
public class TouristDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /** Register a new tourist. Returns true if successful. */
    public boolean register(Tourist tourist) {
        String sql = "INSERT INTO Tourist (TouristName, TouristEmail, TouristPhone, Password) VALUES (?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, tourist.getName());
            ps.setString(2, tourist.getEmail());
            ps.setString(3, tourist.getPhone());
            ps.setString(4, tourist.getPassword());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("TouristDAO.register: " + e.getMessage());
            return false;
        }
    }

    /** Authenticate tourist by email and password. Returns Tourist or null. */
    public Tourist login(String email, String password) {
        String sql = "SELECT * FROM Tourist WHERE TouristEmail=? AND Password=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("TouristDAO.login: " + e.getMessage());
        }
        return null;
    }

    /** Get all tourists. */
    public List<Tourist> getAllTourists() {
        List<Tourist> list = new ArrayList<>();
        String sql = "SELECT * FROM Tourist";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("TouristDAO.getAll: " + e.getMessage());
        }
        return list;
    }

    /** Get tourist by ID. */
    public Tourist getById(int id) {
        String sql = "SELECT * FROM Tourist WHERE TouristID=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("TouristDAO.getById: " + e.getMessage());
        }
        return null;
    }

    /** Update tourist profile. */
    public boolean update(Tourist tourist) {
        String sql = "UPDATE Tourist SET TouristName=?, TouristPhone=? WHERE TouristID=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, tourist.getName());
            ps.setString(2, tourist.getPhone());
            ps.setInt(3, tourist.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("TouristDAO.update: " + e.getMessage());
            return false;
        }
    }

    /** Delete tourist by ID. */
    public boolean delete(int id) {
        String sql = "DELETE FROM Tourist WHERE TouristID=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("TouristDAO.delete: " + e.getMessage());
            return false;
        }
    }

    private Tourist mapRow(ResultSet rs) throws SQLException {
        return new Tourist(
            rs.getInt("TouristID"),
            rs.getString("TouristName"),
            rs.getString("TouristEmail"),
            rs.getString("TouristPhone"),
            rs.getString("Password")
        );
    }
}
