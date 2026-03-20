package com.sahara.dao;

import com.sahara.model.Tour;
import com.sahara.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** TourDAO — handles CRUD for Tour packages. */
public class TourDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /** Get all tours. */
    public List<Tour> getAllTours() {
        List<Tour> list = new ArrayList<>();
        String sql = "SELECT * FROM Tour";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("TourDAO.getAll: " + e.getMessage());
        }
        return list;
    }

    /** Get tour by ID. */
    public Tour getById(String id) {
        String sql = "SELECT * FROM Tour WHERE TourID=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("TourDAO.getById: " + e.getMessage());
        }
        return null;
    }

    /** Add a new tour. */
    public boolean addTour(Tour tour) {
        String sql = "INSERT INTO Tour (TourID, TourName, Duration, Price) VALUES (?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, tour.getTourId());
            ps.setString(2, tour.getTourName());
            ps.setString(3, tour.getDuration());
            ps.setDouble(4, tour.getPrice());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("TourDAO.add: " + e.getMessage());
            return false;
        }
    }

    /** Delete tour by ID. */
    public boolean deleteTour(String id) {
        String sql = "DELETE FROM Tour WHERE TourID=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("TourDAO.delete: " + e.getMessage());
            return false;
        }
    }

    private Tour mapRow(ResultSet rs) throws SQLException {
        return new Tour(
            rs.getString("TourID"),
            rs.getString("TourName"),
            rs.getString("Duration"),
            rs.getDouble("Price")
        );
    }
}
