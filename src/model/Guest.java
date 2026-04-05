// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
// Guest and Admin as two main classes | Repository pattern with SQL | Scanner used | Full Java implementation

package model;

/**
 * Guest — one of the two MAIN classes.
 * Inherits from User. Handles all guest-facing operations.
 */
public class Guest extends User {

    private Membership membership;

    public Guest(String userID, String email) {
        super(userID, email);
    }

    // ── Feature 2: Purchase Ticket ──────────────────────
    public boolean purchaseTicket(Ticket ticket) {
        System.out.println("[GUEST] " + email + " is purchasing ticket: " + ticket.getType());
        return ticket.purchase();
    }

    // ── Feature 3: Book Accommodation ───────────────────
    public boolean bookAccommodation(Accommodation accommodation,
                                     java.time.LocalDate checkIn,
                                     java.time.LocalDate checkOut) {
        System.out.println("[GUEST] " + email + " is booking: " + accommodation.getRoomType());
        return accommodation.book(checkIn, checkOut);
    }

    // ── Feature 4: View Park Info ────────────────────────
    public void viewParkInfo() {
        System.out.println("[PARK INFO] ── Interactive Park Map ──────────────────");
        System.out.println("[PARK INFO]  Roller Coaster   : Wait ≈ 20 min");
        System.out.println("[PARK INFO]  Water Ride        : Wait ≈ 10 min");
        System.out.println("[PARK INFO]  Ferris Wheel      : Wait ≈  5 min");
        System.out.println("[PARK INFO]  Parade Schedule   : 3:00 PM daily");
        System.out.println("[PARK INFO]  Fireworks Show    : 9:00 PM nightly");
        System.out.println("[PARK INFO]  Accessibility     : Wheelchair ramps at all attractions");
        System.out.println("[PARK INFO] ──────────────────────────────────────────");
    }

    // ── Feature 5: Manage Membership ────────────────────
    public void manageMembership(Membership membership) {
        this.membership = membership;
        membership.purchase();
    }

    // ── Feature 6: Submit Feedback ───────────────────────
    public boolean submitFeedback(Feedback feedback) {
        System.out.println("[GUEST] " + email + " is submitting feedback.");
        return feedback.submit();
    }

    // ── Feature 7: Handle Check-In/Out ───────────────────
    public boolean handleCheckIn(CheckInOut checkInOut) {
        System.out.println("[GUEST] " + email + " initiating self-service check-in.");
        return checkInOut.performSelfServiceCheckIn();
    }

    public boolean handleCheckOut(CheckInOut checkInOut) {
        System.out.println("[GUEST] " + email + " initiating self-service check-out.");
        return checkInOut.performSelfServiceCheckOut();
    }

    // Getters & Setters
    public Membership getMembership()                    { return membership; }
    public void       setMembership(Membership m)        { this.membership = m; }
}
