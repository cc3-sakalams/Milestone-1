package model;

import java.time.LocalDate;

public class Accommodation {
    private String accommodationID;
    private String roomType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private boolean available;

    public Accommodation(String accommodationID, String roomType, boolean available) {
        this.accommodationID = accommodationID;
        this.roomType = roomType;
        this.available = available;
    }

    public boolean book(LocalDate checkIn, LocalDate checkOut) {
        if (!available) {
            System.out.println("[ACCOMMODATION] ❌ Room not available: " + roomType);
            System.out.println("[ACCOMMODATION]    Suggestion: Check other room types or dates.");
            return false;
        }
        this.checkInDate = checkIn;
        this.checkOutDate = checkOut;
        this.available = false;
        System.out.println("[ACCOMMODATION] ✅ Room booked!");
        System.out.println("[ACCOMMODATION]    Room Type  : " + roomType);
        System.out.println("[ACCOMMODATION]    Check-in   : " + checkIn);
        System.out.println("[ACCOMMODATION]    Check-out  : " + checkOut);
        return true;
    }

    public String getAccommodationID() { return accommodationID; }
    public String getRoomType() { return roomType; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setCheckInDate(LocalDate d) { this.checkInDate = d; }
    public void setCheckOutDate(LocalDate d) { this.checkOutDate = d; }

    @Override
    public String toString() {
        return String.format("Accommodation{ id=%-6s type=%-15s available=%s }",
                accommodationID, roomType, available ? "YES" : "NO");
    }
}
