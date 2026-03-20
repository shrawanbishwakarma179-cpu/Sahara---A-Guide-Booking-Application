package com.sahara.dao;

import com.sahara.model.Admin;
import com.sahara.util.DatabaseConnection;

import java.sql.*;

/** AdminDAO — handles admin authentication and retrieval. */
public class AdminDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /** Authenticate admin by email and password. */
    public Admin login(String email, String password) {
        String sql = "SELECT * FROM Admin WHERE AdminEmail=? AND Password=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Admin(
                    rs.getInt("AdminID"),
                    rs.getString("AdminName"),
                    rs.getString("AdminEmail"),
                    rs.getString("Password")
                );
            }
        } catch (SQLException e) {
            System.err.println("AdminDAO.login: " + e.getMessage());
        }
        return null;
    }
}
