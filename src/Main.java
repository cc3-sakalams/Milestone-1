// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
// Guest and Admin as two main classes | Repository pattern with SQL | Scanner used | Full Java implementation

import db.DatabaseConnection;
import model.*;
import repository.*;
import repository.impl.*;
import service.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 *   Theme Park + Resort Management System
 *   Interactive Demo Runner
 *   - Two main classes: Guest and Admin (both extend User)
 *   - Repository Pattern with SQLite SQL implementation
 *   - Scanner used for all console input
 * ╚══════════════════════════════════════════════════════════════╝
 */
public class Main {

    // Shared Scanner — one instance for the whole program
    static final Scanner sc = new Scanner(System.in);

    // ── Active session state ───────────────────────────────────
    static Guest loggedInGuest = null;
    static Admin loggedInAdmin = null;

    // ── Services ──────────────────────────────────────────────
    static GuestService guestService;
    static AdminService adminService;

    // ── Repositories (SQL) ────────────────────────────────────
    static UserRepository          userRepo;
    static TicketRepository        ticketRepo;
    static AccommodationRepository accommodationRepo;
    static BookingRepository       bookingRepo;
    static MembershipRepository    membershipRepo;
    static FeedbackRepository      feedbackRepo;
    static ReportRepository        reportRepo;

    public static void main(String[] args) {

        // ── 1. Initialize DB & Repositories ───────────────────
        banner("INITIALIZING SYSTEM");
        DatabaseConnection.getInstance(); // triggers DB + table creation

        userRepo          = new SQLUserRepository();
        ticketRepo        = new SQLTicketRepository();
        accommodationRepo = new SQLAccommodationRepository();
        bookingRepo       = new SQLBookingRepository();
        membershipRepo    = new SQLMembershipRepository();
        feedbackRepo      = new SQLFeedbackRepository();
        reportRepo        = new SQLReportRepository();

        guestService = new GuestService(userRepo, ticketRepo, accommodationRepo,
                                         bookingRepo, membershipRepo, feedbackRepo);
        adminService = new AdminService(userRepo, reportRepo);

        // ── 2. Seed sample data (only if DB is empty) ─────────
        seedData();

        // ── 3. Main Menu Loop ──────────────────────────────────
        boolean running = true;
        while (running) {
            running = showMainMenu();
        }

        // ── 4. Cleanup ─────────────────────────────────────────
        DatabaseConnection.getInstance().close();
        sc.close();
        System.out.println("\n[SYSTEM] Goodbye! 👋");
    }

    // ══════════════════════════════════════════════════════════
    //  MAIN MENU
    // ══════════════════════════════════════════════════════════
    static boolean showMainMenu() {
        banner("THEME PARK + RESORT MANAGEMENT SYSTEM");
        System.out.println("  Who are you?");
        System.out.println("  [1] Guest");
        System.out.println("  [2] Admin");
        System.out.println("  [0] Exit");
        System.out.print("\n  Your choice: ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1" -> guestFlow();
            case "2" -> adminFlow();
            case "0" -> { return false; }
            default  -> System.out.println("  ❌ Invalid choice. Try again.\n");
        }
        return true;
    }

    // ══════════════════════════════════════════════════════════
    //  GUEST FLOW
    // ══════════════════════════════════════════════════════════
    static void guestFlow() {
        banner("GUEST PORTAL");
        System.out.println("  [1] Register as new Guest");
        System.out.println("  [2] Login as existing Guest");
        System.out.println("  [0] Back");
        System.out.print("\n  Your choice: ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1" -> registerGuest();
            case "2" -> loginGuest();
            case "0" -> { return; }
            default  -> System.out.println("  ❌ Invalid choice.\n");
        }

        if (loggedInGuest != null) guestMenu();
    }

    static void registerGuest() {
        section("REGISTER GUEST");
        System.out.print("  Enter User ID (e.g. USR-001): ");
        String id = sc.nextLine().trim();
        System.out.print("  Enter Email              : ");
        String email = sc.nextLine().trim();

        loggedInGuest = guestService.registerGuest(id, email);
        if (loggedInGuest == null) {
            System.out.println("  Registration failed. Returning to menu.\n");
        }
    }

    static void loginGuest() {
        section("LOGIN GUEST");
        System.out.print("  Enter Email: ");
        String email = sc.nextLine().trim();

        userRepo.findByEmail(email).ifPresentOrElse(u -> {
            if (u instanceof Guest g) {
                loggedInGuest = g;
                g.login(email, "");
                System.out.println("  ✅ Welcome back, " + email + "!\n");
            } else {
                System.out.println("  ❌ That account is an Admin account.\n");
            }
        }, () -> System.out.println("  ❌ Email not found. Please register first.\n"));
    }

    static void guestMenu() {
        boolean active = true;
        while (active && loggedInGuest != null) {
            banner("GUEST MENU  ─  " + loggedInGuest.getEmail());
            System.out.println("  [1] Purchase Ticket");
            System.out.println("  [2] Book Accommodation");
            System.out.println("  [3] View Park Info");
            System.out.println("  [4] Manage Membership");
            System.out.println("  [5] Submit Feedback");
            System.out.println("  [6] Check-In");
            System.out.println("  [7] Check-Out");
            System.out.println("  [8] View My Bookings");
            System.out.println("  [0] Logout");
            System.out.print("\n  Your choice: ");
            String ch = sc.nextLine().trim();

            switch (ch) {
                case "1" -> doPurchaseTicket();
                case "2" -> doBookAccommodation();
                case "3" -> doViewParkInfo();
                case "4" -> doManageMembership();
                case "5" -> doSubmitFeedback();
                case "6" -> doCheckIn();
                case "7" -> doCheckOut();
                case "8" -> doViewMyBookings();
                case "0" -> { loggedInGuest = null; active = false;
                              System.out.println("  ✅ Logged out.\n"); }
                default  -> System.out.println("  ❌ Invalid choice.\n");
            }
        }
    }

    // ── Guest Feature 1: Purchase Ticket ──────────────────────
    static void doPurchaseTicket() {
        section("PURCHASE TICKET");
        List<Ticket> available = guestService.getAvailableTickets();
        if (available.isEmpty()) {
            System.out.println("  ❌ No tickets available right now.\n"); return;
        }
        System.out.println("  Available Tickets:");
        available.forEach(t -> System.out.println("   → " + t));
        System.out.print("\n  Enter Ticket ID to purchase: ");
        String ticketID = sc.nextLine().trim();
        guestService.purchaseTicket(loggedInGuest, ticketID);
        pause();
    }

    // ── Guest Feature 2: Book Accommodation ───────────────────
    static void doBookAccommodation() {
        section("BOOK ACCOMMODATION");
        List<Accommodation> rooms = guestService.getAvailableAccommodations();
        if (rooms.isEmpty()) {
            System.out.println("  ❌ No rooms available right now.\n"); return;
        }
        System.out.println("  Available Rooms:");
        rooms.forEach(r -> System.out.println("   → " + r));

        System.out.print("\n  Enter Accommodation ID : ");
        String roomID = sc.nextLine().trim();
        System.out.print("  Check-in  date (YYYY-MM-DD): ");
        LocalDate checkIn = parseDate(sc.nextLine().trim());
        System.out.print("  Check-out date (YYYY-MM-DD): ");
        LocalDate checkOut = parseDate(sc.nextLine().trim());

        if (checkIn == null || checkOut == null) {
            System.out.println("  ❌ Invalid date format. Use YYYY-MM-DD.\n"); return;
        }

        System.out.print("  Bundle a ticket? Enter Ticket ID (or press Enter to skip): ");
        String ticketID = sc.nextLine().trim();

        guestService.bookAccommodation(loggedInGuest, roomID,
                ticketID.isBlank() ? null : ticketID, checkIn, checkOut);
        pause();
    }

    // ── Guest Feature 3: View Park Info ───────────────────────
    static void doViewParkInfo() {
        section("PARK INFORMATION");
        guestService.viewParkInfo(loggedInGuest);
        pause();
    }

    // ── Guest Feature 4: Manage Membership ────────────────────
    static void doManageMembership() {
        section("MANAGE MEMBERSHIP");
        System.out.println("  Membership Types: SEASON_PASS | ANNUAL | VIP");
        System.out.print("  Enter Membership ID (e.g. MEM-001): ");
        String memID = sc.nextLine().trim();
        System.out.print("  Enter Membership Type           : ");
        String type = sc.nextLine().trim().toUpperCase();
        System.out.println("  Enter perks (comma-separated, e.g. Free parking,10% dining discount):");
        System.out.print("  Perks: ");
        String perksInput = sc.nextLine().trim();
        List<String> perks = perksInput.isBlank()
                ? List.of("Standard access")
                : List.of(perksInput.split(","));

        guestService.manageMembership(loggedInGuest, memID, type, perks);
        pause();
    }

    // ── Guest Feature 5: Submit Feedback ──────────────────────
    static void doSubmitFeedback() {
        section("SUBMIT FEEDBACK");
        System.out.print("  Enter Feedback ID (e.g. FB-001): ");
        String fbID = sc.nextLine().trim();
        System.out.print("  Rating (1–5)                   : ");
        int rating = parseIntSafe(sc.nextLine().trim(), 0);
        System.out.print("  Comment                        : ");
        String comment = sc.nextLine().trim();

        guestService.submitFeedback(loggedInGuest, fbID, rating, comment);
        pause();
    }

    // ── Guest Feature 6: Check-In ─────────────────────────────
    static void doCheckIn() {
        section("SELF-SERVICE CHECK-IN");
        System.out.print("  Enter Booking Reference (e.g. BK-USR001-123): ");
        String ref = sc.nextLine().trim();
        guestService.handleCheckIn(loggedInGuest, ref);
        pause();
    }

    // ── Guest Feature 7: Check-Out ────────────────────────────
    static void doCheckOut() {
        section("SELF-SERVICE CHECK-OUT");
        System.out.print("  Enter Booking Reference: ");
        String ref = sc.nextLine().trim();
        guestService.handleCheckOut(loggedInGuest, ref);
        pause();
    }

    // ── Guest Feature 8: View My Bookings ─────────────────────
    static void doViewMyBookings() {
        section("MY BOOKINGS");
        List<Booking> bookings = guestService.getAllBookings().stream()
                .filter(b -> b.getUserID().equals(loggedInGuest.getUserID()))
                .toList();
        if (bookings.isEmpty()) {
            System.out.println("  No bookings found for your account.\n");
        } else {
            bookings.forEach(b -> System.out.println("  → " + b));
        }
        pause();
    }

    // ══════════════════════════════════════════════════════════
    //  ADMIN FLOW
    // ══════════════════════════════════════════════════════════
    static void adminFlow() {
        banner("ADMIN PORTAL");
        System.out.println("  [1] Register as new Admin");
        System.out.println("  [2] Login as existing Admin");
        System.out.println("  [0] Back");
        System.out.print("\n  Your choice: ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1" -> registerAdmin();
            case "2" -> loginAdmin();
            case "0" -> { return; }
            default  -> System.out.println("  ❌ Invalid choice.\n");
        }

        if (loggedInAdmin != null) adminMenu();
    }

    static void registerAdmin() {
        section("REGISTER ADMIN");
        System.out.print("  Enter Admin ID (e.g. ADM-001): ");
        String id = sc.nextLine().trim();
        System.out.print("  Enter Email                 : ");
        String email = sc.nextLine().trim();

        loggedInAdmin = adminService.registerAdmin(id, email);
        if (loggedInAdmin == null) {
            System.out.println("  Registration failed. Returning to menu.\n");
        }
    }

    static void loginAdmin() {
        section("LOGIN ADMIN");
        System.out.print("  Enter Email: ");
        String email = sc.nextLine().trim();

        userRepo.findByEmail(email).ifPresentOrElse(u -> {
            if (u instanceof Admin a) {
                loggedInAdmin = a;
                a.login(email, "");
                System.out.println("  ✅ Welcome back, Admin " + email + "!\n");
            } else {
                System.out.println("  ❌ That account is a Guest account.\n");
            }
        }, () -> System.out.println("  ❌ Email not found. Please register first.\n"));
    }

    static void adminMenu() {
        boolean active = true;
        while (active && loggedInAdmin != null) {
            banner("ADMIN MENU  ─  " + loggedInAdmin.getEmail());
            System.out.println("  [1] Generate Sales Report");
            System.out.println("  [2] Generate Occupancy Report");
            System.out.println("  [3] Generate Visitor Stats Report");
            System.out.println("  [4] Generate Performance Report");
            System.out.println("  [5] View All Reports");
            System.out.println("  [6] View All Users");
            System.out.println("  [0] Logout");
            System.out.print("\n  Your choice: ");
            String ch = sc.nextLine().trim();

            switch (ch) {
                case "1" -> adminService.generateReport(loggedInAdmin, "SALES");
                case "2" -> adminService.generateReport(loggedInAdmin, "OCCUPANCY");
                case "3" -> adminService.generateReport(loggedInAdmin, "VISITOR_STATS");
                case "4" -> adminService.generateReport(loggedInAdmin, "PERFORMANCE");
                case "5" -> doViewAllReports();
                case "6" -> doViewAllUsers();
                case "0" -> { loggedInAdmin = null; active = false;
                              System.out.println("  ✅ Admin logged out.\n"); }
                default  -> System.out.println("  ❌ Invalid choice.\n");
            }
            if (ch.matches("[1-4]")) pause();
        }
    }

    static void doViewAllReports() {
        section("ALL REPORTS");
        List<Report> reports = adminService.getAllReports();
        if (reports.isEmpty()) System.out.println("  No reports generated yet.");
        else reports.forEach(r -> System.out.println("  → " + r));
        pause();
    }

    static void doViewAllUsers() {
        section("ALL USERS");
        List<model.User> users = adminService.getAllUsers();
        if (users.isEmpty()) System.out.println("  No users found.");
        else users.forEach(u -> System.out.println("  → " + u));
        pause();
    }

    // ══════════════════════════════════════════════════════════
    //  SEED DATA (runs only if DB is empty)
    // ══════════════════════════════════════════════════════════
    static void seedData() {
        if (!ticketRepo.findAll().isEmpty()) {
            System.out.println("[SEED] Data already exists in DB. Skipping seed.\n");
            return;
        }
        System.out.println("[SEED] Seeding initial data...");

        // Tickets
        ticketRepo.save(new Ticket("T001", "SINGLE_DAY",  40.00f, 100));
        ticketRepo.save(new Ticket("T002", "MULTI_DAY",   85.00f,  50));
        ticketRepo.save(new Ticket("T003", "DAY_PASS",    25.00f, 200));

        // Accommodations
        accommodationRepo.save(new Accommodation("A001", "Deluxe Room",    true));
        accommodationRepo.save(new Accommodation("A002", "Villa Suite",     true));
        accommodationRepo.save(new Accommodation("A003", "Standard Room",  true));
        accommodationRepo.save(new Accommodation("A004", "Family Bungalow", true));

        System.out.println("[SEED] ✅ Sample tickets and accommodations loaded.\n");
    }

    // ══════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════
    static void banner(String title) {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.printf( "║  %-52s║%n", title);
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }

    static void section(String title) {
        System.out.println("\n┌──────────────────────────────────────────────────┐");
        System.out.printf( "│  %-48s│%n", title);
        System.out.println("└──────────────────────────────────────────────────┘");
    }

    static void pause() {
        System.out.print("\n  [Press Enter to continue]");
        sc.nextLine();
    }

    static LocalDate parseDate(String s) {
        try { return LocalDate.parse(s); }
        catch (Exception e) { return null; }
    }

    static int parseIntSafe(String s, int fallback) {
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { return fallback; }
    }
}
