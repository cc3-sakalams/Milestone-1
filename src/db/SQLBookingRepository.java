
package repository.impl;

import db.DatabaseConnection;
import model.Booking;
import repository.BookingRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SQLBookingRepository implements BookingRepository {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(Booking b) {
        String sql = """
            INSERT OR IGNORE INTO bookings
              (bookingID, bookingReference, userID, checkInDate, checkOutDate, status, accommodationID)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, b.getBookingID());
            ps.setString(2, b.getBookingReference());
            ps.setString(3, b.getUserID());
            ps.setString(4, b.getCheckInDate().toString());
            ps.setString(5, b.getCheckOutDate().toString());
            ps.setString(6, b.getStatus());
            ps.setString(7, b.getAccommodation() != null ? b.getAccommodation().getAccommodationID() : null);
            ps.executeUpdate();
            System.out.println("[DB] Booking saved: " + b.getBookingReference());

            // Save linked tickets in join table
            for (var ticket : b.getTickets()) {
                saveBookingTicket(b.getBookingID(), ticket.getTicketID());
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error saving booking: " + e.getMessage());
        }
    }

    private void saveBookingTicket(String bookingID, String ticketID) {
        String sql = "INSERT OR IGNORE INTO booking_tickets (bookingID, ticketID) VALUES (?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, bookingID);
            ps.setString(2, ticketID);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB] Error saving booking_ticket link: " + e.getMessage());
        }
    }

    @Override
    public Optional<Booking> findById(String id) {
        String sql = "SELECT * FROM bookings WHERE bookingID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error finding booking by id: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Booking> findByReference(String ref) {
        String sql = "SELECT * FROM bookings WHERE bookingReference = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, ref);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error finding booking by reference: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Booking> findAll() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error fetching all bookings: " + e.getMessage());
        }
        return list;
    }

    @Override
    public void update(Booking b) {
        String sql = "UPDATE bookings SET status = ?, checkInDate = ?, checkOutDate = ?, accommodationID = ? WHERE bookingID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, b.getStatus());
            ps.setString(2, b.getCheckInDate().toString());
            ps.setString(3, b.getCheckOutDate().toString());
            ps.setString(4, b.getAccommodation() != null ? b.getAccommodation().getAccommodationID() : null);
            ps.setString(5, b.getBookingID());
            ps.executeUpdate();
            System.out.println("[DB] Booking updated: " + b.getBookingID());
        } catch (SQLException e) {
            System.err.println("[DB] Error updating booking: " + e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM bookings WHERE bookingID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
            System.out.println("[DB] Booking deleted: " + id);
        } catch (SQLException e) {
            System.err.println("[DB] Error deleting booking: " + e.getMessage());
        }
    }

    private Booking mapRow(ResultSet rs) throws SQLException {
        return new Booking(
                rs.getString("bookingID"),
                rs.getString("bookingReference"),
                rs.getString("userID"),
                LocalDate.parse(rs.getString("checkInDate")),
                LocalDate.parse(rs.getString("checkOutDate"))
        );
        // Note: status is set via setStatus; tickets & accommodation
        // are loaded by services when needed (lazy approach)
    }
}
