package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Booking {
    private String bookingID;
    private String bookingReference;
    private String userID;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status;
    private List<Ticket> tickets;
    private Accommodation accommodation;
    private CheckInOut checkInOut;

    public Booking(String bookingID, String bookingReference, String userID,
                   LocalDate checkInDate, LocalDate checkOutDate) {
        this.bookingID = bookingID;
        this.bookingReference = bookingReference;
        this.userID = userID;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = "PENDING";
        this.tickets = new ArrayList<>();
    }

    public void confirmReservation() {
        this.status = "CONFIRMED";
        System.out.println("[BOOKING] ✅ Reservation confirmed!");
        System.out.println("[BOOKING]    Reference  : " + bookingReference);
        System.out.println("[BOOKING]    Check-in   : " + checkInDate);
        System.out.println("[BOOKING]    Check-out  : " + checkOutDate);
    }

    public boolean checkIn() {
        if (!status.equals("CONFIRMED")) {
            System.out.println("[BOOKING] ❌ Cannot check in — current status: " + status);
            return false;
        }
        this.status = "CHECKED_IN";
        System.out.println("[BOOKING] ✅ Status updated → CHECKED_IN");
        return true;
    }

    public boolean checkOut() {
        if (!status.equals("CHECKED_IN")) {
            System.out.println("[BOOKING] ❌ Cannot check out — current status: " + status);
            return false;
        }
        this.status = "CHECKED_OUT";
        System.out.println("[BOOKING] ✅ Status updated → CHECKED_OUT");
        return true;
    }

    public void addTicket(Ticket ticket) { 
        tickets.add(ticket); 
    }

    public String getBookingID() { return bookingID; }
    public String getBookingReference() { return bookingReference; }
    public String getUserID() { return userID; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public String getStatus() { return status; }
    public List<Ticket> getTickets() { return tickets; }
    public Accommodation getAccommodation() { return accommodation; }
    public CheckInOut getCheckInOut() { return checkInOut; }

    public void setStatus(String status) { this.status = status; }
    public void setAccommodation(Accommodation a) { this.accommodation = a; }
    public void setCheckInOut(CheckInOut c) { this.checkInOut = c; }

    @Override
    public String toString() {
        return String.format("Booking{ ref=%-20s status=%-12s checkIn=%s checkOut=%s }",
                bookingReference, status, checkInDate, checkOutDate);
    }
}
