package repository.impl;

import db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.Admin;
import model.Guest;
import model.User;
import repository.UserRepository;

public class SQLUserRepository implements UserRepository {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(User user) {
        String sql = "INSERT OR IGNORE INTO users (userID, email, sessionToken, userType) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, user.getUserID());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getSessionToken());
            ps.setString(4, user instanceof Admin ? "ADMIN" : "GUEST");
            ps.executeUpdate();
            System.out.println("[DB] User saved: " + user.getEmail());
        } catch (SQLException e) {
            System.err.println("[DB] Error saving user: " + e.getMessage());
        }
    }

    @Override
    public Optional<User> findById(String id) {
        String sql = "SELECT * FROM users WHERE userID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error finding user by id: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error finding user by email: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) users.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error fetching all users: " + e.getMessage());
        }
        return users;
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE users SET email = ?, sessionToken = ? WHERE userID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getSessionToken());
            ps.setString(3, user.getUserID());
            ps.executeUpdate();
            System.out.println("[DB] User updated: " + user.getUserID());
        } catch (SQLException e) {
            System.err.println("[DB] Error updating user: " + e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM users WHERE userID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
            System.out.println("[DB] User deleted: " + id);
        } catch (SQLException e) {
            System.err.println("[DB] Error deleting user: " + e.getMessage());
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        String userID   = rs.getString("userID");
        String email    = rs.getString("email");
        String token    = rs.getString("sessionToken");
        String userType = rs.getString("userType");
        User user;
        if ("ADMIN".equals(userType)) {
            user = new Admin(userID, email);
        } else {
            user = new Guest(userID, email);
        }
        user.setSessionToken(token != null ? token : "");
        return user;
    }
}
