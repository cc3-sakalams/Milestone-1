package repository.impl;

import db.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.Report;
import repository.ReportRepository;

public class SQLReportRepository implements ReportRepository {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(Report r) {
        String sql = "INSERT OR IGNORE INTO reports (reportID, type, content, generatedAt) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, r.getReportID());
            ps.setString(2, r.getType());
            ps.setString(3, r.getContent());
            ps.setString(4, r.getGeneratedAt() != null ? r.getGeneratedAt().toString() : null);
            ps.executeUpdate();
            System.out.println("[DB] Report saved: " + r.getReportID());
        } catch (SQLException e) {
            System.err.println("[DB] Error saving report: " + e.getMessage());
        }
    }

    @Override
    public Optional<Report> findById(String id) {
        String sql = "SELECT * FROM reports WHERE reportID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error finding report: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Report> findAll() {
        List<Report> list = new ArrayList<>();
        String sql = "SELECT * FROM reports";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error fetching reports: " + e.getMessage());
        }
        return list;
    }

    @Override
    public void update(Report r) {
        String sql = "UPDATE reports SET type = ?, content = ?, generatedAt = ? WHERE reportID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, r.getType());
            ps.setString(2, r.getContent());
            ps.setString(3, r.getGeneratedAt() != null ? r.getGeneratedAt().toString() : null);
            ps.setString(4, r.getReportID());
            ps.executeUpdate();
            System.out.println("[DB] Report updated: " + r.getReportID());
        } catch (SQLException e) {
            System.err.println("[DB] Error updating report: " + e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM reports WHERE reportID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
            System.out.println("[DB] Report deleted: " + id);
        } catch (SQLException e) {
            System.err.println("[DB] Error deleting report: " + e.getMessage());
        }
    }

    private Report mapRow(ResultSet rs) throws SQLException {
        Report r = new Report(rs.getString("reportID"), rs.getString("type"));
        r.setContent(rs.getString("content"));
        String dt = rs.getString("generatedAt");
        if (dt != null) r.setGeneratedAt(LocalDateTime.parse(dt));
        return r;
    }
}
