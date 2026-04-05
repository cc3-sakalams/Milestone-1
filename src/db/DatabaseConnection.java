// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
// Guest and Admin as two main classes | Repository pattern with SQL | Scanner used | Full Java implementation

package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseConnection — Singleton that manages one SQLite JDBC connection.
 * All repositories share this single connection.
 * Database file: themepark.db (created in working directory on first run)
 */
public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:themepark.db";
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(DB_URL);
            this.connection.setAutoCommit(true);
            System.out.println("[DB] ✅ SQLite connection established → themepark.db");
            createAllTables();
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] ❌ SQLite JDBC driver not found. Add sqlite-jdbc jar to classpath.");
            throw new RuntimeException(e);
        } catch (SQLException e) {
            System.err.println("[DB] ❌ Failed to connect: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /** Returns the singleton instance. Thread-safe via synchronized. */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() { return connection; }

    /** Creates all tables if they do not already exist. */
    private void createAllTables() throws SQLException {
        Statement stmt = connection.createStatement();

        // ── users ────────────────────────────────────────────────
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS users (
                userID       TEXT PRIMARY KEY,
                email        TEXT UNIQUE NOT NULL,
                sessionToken TEXT,
                userType     TEXT NOT NULL   -- 'GUEST' or 'ADMIN'
            )
        """);

        // ── tickets ──────────────────────────────────────────────
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS tickets (
                ticketID     TEXT PRIMARY KEY,
                type         TEXT NOT NULL,
                price        REAL NOT NULL,
                availability INTEGER NOT NULL,
                qrCode       TEXT
            )
        """);

        // ── accommodations ───────────────────────────────────────
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS accommodations (
                accommodationID TEXT PRIMARY KEY,
                roomType        TEXT NOT NULL,
                checkInDate     TEXT,
                checkOutDate    TEXT,
                available       INTEGER NOT NULL DEFAULT 1
            )
        """);

        // ── bookings ─────────────────────────────────────────────
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS bookings (
                bookingID        TEXT PRIMARY KEY,
                bookingReference TEXT UNIQUE NOT NULL,
                userID           TEXT NOT NULL,
                checkInDate      TEXT NOT NULL,
                checkOutDate     TEXT NOT NULL,
                status           TEXT NOT NULL DEFAULT 'PENDING',
                accommodationID  TEXT,
                FOREIGN KEY (userID) REFERENCES users(userID),
                FOREIGN KEY (accommodationID) REFERENCES accommodations(accommodationID)
            )
        """);

        // ── booking_tickets (join table) ─────────────────────────
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS booking_tickets (
                bookingID TEXT NOT NULL,
                ticketID  TEXT NOT NULL,
                PRIMARY KEY (bookingID, ticketID),
                FOREIGN KEY (bookingID) REFERENCES bookings(bookingID),
                FOREIGN KEY (ticketID)  REFERENCES tickets(ticketID)
            )
        """);

        // ── memberships ──────────────────────────────────────────
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS memberships (
                membershipID TEXT PRIMARY KEY,
                userID       TEXT NOT NULL,
                type         TEXT NOT NULL,
                active       INTEGER NOT NULL DEFAULT 0,
                perks        TEXT,
                FOREIGN KEY (userID) REFERENCES users(userID)
            )
        """);

        // ── feedbacks ────────────────────────────────────────────
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS feedbacks (
                feedbackID TEXT PRIMARY KEY,
                userID     TEXT NOT NULL,
                rating     INTEGER NOT NULL,
                comment    TEXT NOT NULL,
                FOREIGN KEY (userID) REFERENCES users(userID)
            )
        """);

        // ── reports ──────────────────────────────────────────────
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS reports (
                reportID    TEXT PRIMARY KEY,
                type        TEXT NOT NULL,
                content     TEXT,
                generatedAt TEXT
            )
        """);

        // ── checkinout ───────────────────────────────────────────
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS checkinout (
                reference  TEXT PRIMARY KEY,
                status     TEXT NOT NULL DEFAULT 'PENDING',
                digitalKey TEXT
            )
        """);

        stmt.close();
        System.out.println("[DB] ✅ All tables verified/created.");
    }

    /** Close connection on shutdown. */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing connection: " + e.getMessage());
        }
    }
}
