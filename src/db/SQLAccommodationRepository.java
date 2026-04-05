
package repository.impl;

import db.DatabaseConnection;
import model.Accommodation;
import repository.AccommodationRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SQLAccommodationRepository implements AccommodationRepository {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(Accommodation a) {
        String sql = "INSERT OR IGNORE INTO accommodations (accommodationID, roomType, checkInDate, checkOutDate, available) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, a.getAccommodationID());
            ps.setString(2, a.getRoomType());
            ps.setString(3, a.getCheckInDate()  != null ? a.getCheckInDate().toString()  : null);
            ps.setString(4, a.getCheckOutDate() != null ? a.getCheckOutDate().toString() : null);
            ps.setInt(5, a.isAvailable() ? 1 : 0);
            ps.executeUpdate();
            System.out.println("[DB] Accommodation saved: " + a.getAccommodationID());
        } catch (SQLException e) {
            System.err.println("[DB] Error saving accommodation: " + e.getMessage());
        }
    }

    @Override
    public Optional<Accommodation> findById(String id) {
        String sql = "SELECT * FROM accommodations WHERE accommodationID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error finding accommodation: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Accommodation> findAll() {
        List<Accommodation> list = new ArrayList<>();
        String sql = "SELECT * FROM accommodations";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error fetching accommodations: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Accommodation> findAvailable() {
        List<Accommodation> list = new ArrayList<>();
        String sql = "SELECT * FROM accommodations WHERE available = 1";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error fetching available accommodations: " + e.getMessage());
        }
        return list;
    }

    @Override
    public void update(Accommodation a) {
        String sql = "UPDATE accommodations SET roomType = ?, checkInDate = ?, checkOutDate = ?, available = ? WHERE accommodationID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, a.getRoomType());
            ps.setString(2, a.getCheckInDate()  != null ? a.getCheckInDate().toString()  : null);
            ps.setString(3, a.getCheckOutDate() != null ? a.getCheckOutDate().toString() : null);
            ps.setInt(4, a.isAvailable() ? 1 : 0);
            ps.setString(5, a.getAccommodationID());
            ps.executeUpdate();
            System.out.println("[DB] Accommodation updated: " + a.getAccommodationID());
        } catch (SQLException e) {
            System.err.println("[DB] Error updating accommodation: " + e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM accommodations WHERE accommodationID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
            System.out.println("[DB] Accommodation deleted: " + id);
        } catch (SQLException e) {
            System.err.println("[DB] Error deleting accommodation: " + e.getMessage());
        }
    }

    private Accommodation mapRow(ResultSet rs) throws SQLException {
        Accommodation a = new Accommodation(
                rs.getString("accommodationID"),
                rs.getString("roomType"),
                rs.getInt("available") == 1
        );
        String ci = rs.getString("checkInDate");
        String co = rs.getString("checkOutDate");
        if (ci != null) a.setCheckInDate(LocalDate.parse(ci));
        if (co != null) a.setCheckOutDate(LocalDate.parse(co));
        return a;
    }
}
