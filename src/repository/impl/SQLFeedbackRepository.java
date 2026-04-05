// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
// Guest and Admin as two main classes | Repository pattern with SQL | Scanner used | Full Java implementation

package repository.impl;

import db.DatabaseConnection;
import model.Feedback;
import repository.FeedbackRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SQLFeedbackRepository implements FeedbackRepository {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(Feedback f) {
        String sql = "INSERT OR IGNORE INTO feedbacks (feedbackID, userID, rating, comment) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, f.getFeedbackID());
            ps.setString(2, f.getUserID());
            ps.setInt(3, f.getRating());
            ps.setString(4, f.getComment());
            ps.executeUpdate();
            System.out.println("[DB] Feedback saved: " + f.getFeedbackID());
        } catch (SQLException e) {
            System.err.println("[DB] Error saving feedback: " + e.getMessage());
        }
    }

    @Override
    public Optional<Feedback> findById(String id) {
        String sql = "SELECT * FROM feedbacks WHERE feedbackID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error finding feedback: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Feedback> findAll() {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT * FROM feedbacks";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error fetching feedbacks: " + e.getMessage());
        }
        return list;
    }

    @Override
    public void update(Feedback f) {
        String sql = "UPDATE feedbacks SET rating = ?, comment = ? WHERE feedbackID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, f.getRating());
            ps.setString(2, f.getComment());
            ps.setString(3, f.getFeedbackID());
            ps.executeUpdate();
            System.out.println("[DB] Feedback updated: " + f.getFeedbackID());
        } catch (SQLException e) {
            System.err.println("[DB] Error updating feedback: " + e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM feedbacks WHERE feedbackID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
            System.out.println("[DB] Feedback deleted: " + id);
        } catch (SQLException e) {
            System.err.println("[DB] Error deleting feedback: " + e.getMessage());
        }
    }

    private Feedback mapRow(ResultSet rs) throws SQLException {
        return new Feedback(
                rs.getString("feedbackID"),
                rs.getString("userID"),
                rs.getInt("rating"),
                rs.getString("comment")
        );
    }
}
