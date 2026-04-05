// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
// Guest and Admin as two main classes | Repository pattern with SQL | Scanner used | Full Java implementation

package repository.impl;

import db.DatabaseConnection;
import model.Ticket;
import repository.TicketRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SQLTicketRepository implements TicketRepository {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(Ticket ticket) {
        String sql = "INSERT OR IGNORE INTO tickets (ticketID, type, price, availability, qrCode) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, ticket.getTicketID());
            ps.setString(2, ticket.getType());
            ps.setDouble(3, ticket.getPrice());
            ps.setInt(4, ticket.getAvailability());
            ps.setString(5, ticket.getQrCode());
            ps.executeUpdate();
            System.out.println("[DB] Ticket saved: " + ticket.getTicketID());
        } catch (SQLException e) {
            System.err.println("[DB] Error saving ticket: " + e.getMessage());
        }
    }

    @Override
    public Optional<Ticket> findById(String id) {
        String sql = "SELECT * FROM tickets WHERE ticketID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error finding ticket: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Ticket> findAll() {
        List<Ticket> list = new ArrayList<>();
        String sql = "SELECT * FROM tickets";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error fetching tickets: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Ticket> findAvailable() {
        List<Ticket> list = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE availability > 0";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error fetching available tickets: " + e.getMessage());
        }
        return list;
    }

    @Override
    public void update(Ticket ticket) {
        String sql = "UPDATE tickets SET type = ?, price = ?, availability = ?, qrCode = ? WHERE ticketID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, ticket.getType());
            ps.setDouble(2, ticket.getPrice());
            ps.setInt(3, ticket.getAvailability());
            ps.setString(4, ticket.getQrCode());
            ps.setString(5, ticket.getTicketID());
            ps.executeUpdate();
            System.out.println("[DB] Ticket updated: " + ticket.getTicketID());
        } catch (SQLException e) {
            System.err.println("[DB] Error updating ticket: " + e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM tickets WHERE ticketID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
            System.out.println("[DB] Ticket deleted: " + id);
        } catch (SQLException e) {
            System.err.println("[DB] Error deleting ticket: " + e.getMessage());
        }
    }

    private Ticket mapRow(ResultSet rs) throws SQLException {
        Ticket t = new Ticket(
                rs.getString("ticketID"),
                rs.getString("type"),
                rs.getFloat("price"),
                rs.getInt("availability")
        );
        t.setQrCode(rs.getString("qrCode") != null ? rs.getString("qrCode") : "");
        return t;
    }
}
