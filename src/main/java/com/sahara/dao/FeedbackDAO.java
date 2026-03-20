package com.sahara.dao;

import com.sahara.model.Feedback;
import com.sahara.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** FeedbackDAO — handles feedback/rating storage and retrieval. */
public class FeedbackDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /** Submit feedback for a guide. */
    public boolean submitFeedback(Feedback feedback) {
        String sql = "INSERT INTO Feedback (TouristID, GuideID, BookingID, Rating, Comment, FeedbackDate) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, feedback.getTouristId());
            ps.setInt(2, feedback.getGuideId());
            ps.setInt(3, feedback.getBookingId());
            ps.setInt(4, feedback.getRating());
            ps.setString(5, feedback.getComment());
            ps.setDate(6, Date.valueOf(feedback.getFeedbackDate()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("FeedbackDAO.submit: " + e.getMessage());
            return false;
        }
    }

    /** Get average rating for a guide. */
    public double getAverageRating(int guideId) {
        String sql = "SELECT AVG(Rating) FROM Feedback WHERE GuideID=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, guideId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("FeedbackDAO.avgRating: " + e.getMessage());
        }
        return 0.0;
    }

    /** Get all feedback for a guide. */
    public List<Feedback> getFeedbackForGuide(int guideId) {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT f.*, t.TouristName, g.GuideName FROM Feedback f " +
                     "JOIN Tourist t ON f.TouristID=t.TouristID " +
                     "JOIN Guide g ON f.GuideID=g.GuideID " +
                     "WHERE f.GuideID=? ORDER BY f.FeedbackDate DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, guideId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Feedback fb = new Feedback();
                fb.setFeedbackId(rs.getInt("FeedbackID"));
                fb.setTouristId(rs.getInt("TouristID"));
                fb.setGuideId(rs.getInt("GuideID"));
                fb.setBookingId(rs.getInt("BookingID"));
                fb.setRating(rs.getInt("Rating"));
                fb.setComment(rs.getString("Comment"));
                fb.setFeedbackDate(rs.getDate("FeedbackDate").toLocalDate());
                fb.setTouristName(rs.getString("TouristName"));
                fb.setGuideName(rs.getString("GuideName"));
                list.add(fb);
            }
        } catch (SQLException e) {
            System.err.println("FeedbackDAO.getForGuide: " + e.getMessage());
        }
        return list;
    }
}
