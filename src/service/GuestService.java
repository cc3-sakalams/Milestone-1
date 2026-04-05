// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
// Guest and Admin as two main classes | Repository pattern with SQL | Scanner used | Full Java implementation

package service;

import model.*;
import repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * GuestService — orchestrates all guest use cases.
 * Guest is one of the two MAIN classes (extends User).
 * All persistence goes through SQL repositories.
 */
public class GuestService {

    private final UserRepository          userRepo;
    private final TicketRepository        ticketRepo;
    private final AccommodationRepository accommodationRepo;
    private final BookingRepository       bookingRepo;
    private final MembershipRepository    membershipRepo;
    private final FeedbackRepository      feedbackRepo;

    public GuestService(UserRepository userRepo,
                        TicketRepository ticketRepo,
                        AccommodationRepository accommodationRepo,
                        BookingRepository bookingRepo,
                        MembershipRepository membershipRepo,
                        FeedbackRepository feedbackRepo) {
        this.userRepo          = userRepo;
        this.ticketRepo        = ticketRepo;
        this.accommodationRepo = accommodationRepo;
        this.bookingRepo       = bookingRepo;
        this.membershipRepo    = membershipRepo;
        this.feedbackRepo      = feedbackRepo;
    }

    // ══════════════════════════════════════════════════════
    //  FEATURE 1: Register Guest
    // ══════════════════════════════════════════════════════
    public Guest registerGuest(String userID, String email) {
        if (userRepo.findByEmail(email).isPresent()) {
            System.out.println("[GUEST SERVICE] ❌ Email already registered: " + email);
            return null;
        }
        Guest guest = new Guest(userID, email);
        guest.register();
        guest.issueSessionToken();
        userRepo.save(guest);
        System.out.println("[GUEST SERVICE] ✅ Guest saved to DB.\n");
        return guest;
    }

    // ══════════════════════════════════════════════════════
    //  FEATURE 2: Purchase Ticket
    // ══════════════════════════════════════════════════════
    public boolean purchaseTicket(Guest guest, String ticketID) {
        Optional<Ticket> opt = ticketRepo.findById(ticketID);
        if (opt.isEmpty()) {
            System.out.println("[GUEST SERVICE] ❌ Ticket not found: " + ticketID);
            return false;
        }
        Ticket ticket = opt.get();
        boolean success = guest.purchaseTicket(ticket);
        if (success) ticketRepo.update(ticket);
        return success;
    }

    // ══════════════════════════════════════════════════════
    //  FEATURE 3: Book Accommodation (with optional ticket bundle)
    // ══════════════════════════════════════════════════════
    public Booking bookAccommodation(Guest guest,
                                     String accommodationID,
                                     String ticketID,
                                     LocalDate checkIn,
                                     LocalDate checkOut) {
        Optional<Accommodation> roomOpt = accommodationRepo.findById(accommodationID);
        if (roomOpt.isEmpty() || !roomOpt.get().isAvailable()) {
            System.out.println("[GUEST SERVICE] ❌ Accommodation unavailable. Available rooms:");
            accommodationRepo.findAvailable()
                    .forEach(a -> System.out.println("   → " + a));
            return null;
        }

        Accommodation room = roomOpt.get();
        guest.bookAccommodation(room, checkIn, checkOut);
        accommodationRepo.update(room);

        String bookingID  = "BKG-" + System.currentTimeMillis();
        String bookingRef = "BK-" + guest.getUserID().toUpperCase() + "-" + System.currentTimeMillis() % 1000;
        Booking booking   = new Booking(bookingID, bookingRef, guest.getUserID(), checkIn, checkOut);
        booking.setAccommodation(room);

        // Bundle ticket if provided
        if (ticketID != null && !ticketID.isBlank()) {
            ticketRepo.findById(ticketID).ifPresent(t -> {
                boolean ok = guest.purchaseTicket(t);
                if (ok) {
                    ticketRepo.update(t);
                    booking.addTicket(t);
                }
            });
        }

        booking.confirmReservation();
        bookingRepo.save(booking);
        System.out.println("[GUEST SERVICE] 📧 Confirmation email sent to: " + guest.getEmail() + "\n");
        return booking;
    }

    // ══════════════════════════════════════════════════════
    //  FEATURE 4: View Park Info
    // ══════════════════════════════════════════════════════
    public void viewParkInfo(Guest guest) {
        guest.viewParkInfo();
    }

    // ══════════════════════════════════════════════════════
    //  FEATURE 5: Manage Membership
    // ══════════════════════════════════════════════════════
    public Membership manageMembership(Guest guest, String membershipID,
                                       String type, List<String> perks) {
        Membership membership = new Membership(membershipID, guest.getUserID(), type);
        perks.forEach(membership::addPerk);
        guest.manageMembership(membership);
        membershipRepo.save(membership);
        System.out.println("[GUEST SERVICE] ✅ Membership stored in DB.\n");
        return membership;
    }

    // ══════════════════════════════════════════════════════
    //  FEATURE 6: Submit Feedback
    // ══════════════════════════════════════════════════════
    public boolean submitFeedback(Guest guest, String feedbackID, int rating, String comment) {
        Feedback feedback = new Feedback(feedbackID, guest.getUserID(), rating, comment);
        boolean success = guest.submitFeedback(feedback);
        if (success) {
            feedbackRepo.save(feedback);
            System.out.println("[GUEST SERVICE] ✅ Feedback stored in DB.\n");
        }
        return success;
    }

    // ══════════════════════════════════════════════════════
    //  FEATURE 7: Handle Check-In
    // ══════════════════════════════════════════════════════
    public boolean handleCheckIn(Guest guest, String bookingReference) {
        Optional<Booking> bookingOpt = bookingRepo.findByReference(bookingReference);
        if (bookingOpt.isEmpty()) {
            System.out.println("[GUEST SERVICE] ❌ Booking not found: " + bookingReference);
            System.out.println("[GUEST SERVICE] ⚠️  Please visit the front desk for staff assistance.\n");
            return false;
        }
        Booking    booking    = bookingOpt.get();
        CheckInOut checkInOut = new CheckInOut(bookingReference);
        boolean    success    = guest.handleCheckIn(checkInOut);
        if (success) {
            booking.checkIn();
            booking.setCheckInOut(checkInOut);
            bookingRepo.update(booking);
            System.out.println("[GUEST SERVICE] ✅ Check-in status saved to DB.\n");
        }
        return success;
    }

    // ══════════════════════════════════════════════════════
    //  FEATURE 7: Handle Check-Out
    // ══════════════════════════════════════════════════════
    public boolean handleCheckOut(Guest guest, String bookingReference) {
        Optional<Booking> bookingOpt = bookingRepo.findByReference(bookingReference);
        if (bookingOpt.isEmpty()) {
            System.out.println("[GUEST SERVICE] ❌ Booking not found: " + bookingReference);
            return false;
        }
        Booking booking = bookingOpt.get();
        if (!"CHECKED_IN".equals(booking.getStatus())) {
            System.out.println("[GUEST SERVICE] ❌ Cannot check out — booking status is: " + booking.getStatus());
            return false;
        }
        CheckInOut checkInOut = new CheckInOut(bookingReference);
        checkInOut.performSelfServiceCheckIn();   // re-validate and re-grant to set CHECKED_IN state
        boolean success = guest.handleCheckOut(checkInOut);
        if (success) {
            booking.checkOut();
            bookingRepo.update(booking);
            System.out.println("[GUEST SERVICE] ✅ Check-out status saved to DB.\n");
        }
        return success;
    }

    // ── Repository accessors (used by Main for listing) ──
    public List<Ticket>        getAvailableTickets()        { return ticketRepo.findAvailable(); }
    public List<Accommodation> getAvailableAccommodations() { return accommodationRepo.findAvailable(); }
    public List<Booking>       getAllBookings()              { return bookingRepo.findAll(); }
}
