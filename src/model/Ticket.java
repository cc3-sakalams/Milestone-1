// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
// Guest and Admin as two main classes | Repository pattern with SQL | Scanner used | Full Java implementation

package model;

public class Ticket {
    private String ticketID;
    private String type;          // SINGLE_DAY | MULTI_DAY | DAY_PASS
    private float  price;
    private int    availability;
    private String qrCode;

    public Ticket(String ticketID, String type, float price, int availability) {
        this.ticketID     = ticketID;
        this.type         = type;
        this.price        = price;
        this.availability = availability;
        this.qrCode       = "";
    }

    public boolean purchase() {
        if (availability <= 0) {
            System.out.println("[TICKET] ❌ No availability for type: " + type);
            System.out.println("[TICKET]    Suggestion: Try a different ticket type or date.");
            return false;
        }
        availability--;
        this.qrCode = generateQRCode();
        System.out.println("[TICKET] ✅ Ticket purchased!");
        System.out.println("[TICKET]    Type      : " + type);
        System.out.println("[TICKET]    Price     : $" + String.format("%.2f", price));
        System.out.println("[TICKET]    QR Code   : " + qrCode);
        return true;
    }

    public String generateQRCode() {
        return "QR-" + ticketID + "-" + System.currentTimeMillis();
    }

    // Getters & Setters
    public String getTicketID()                { return ticketID; }
    public String getType()                    { return type; }
    public float  getPrice()                   { return price; }
    public int    getAvailability()            { return availability; }
    public String getQrCode()                  { return qrCode; }
    public void   setAvailability(int a)       { this.availability = a; }
    public void   setQrCode(String qrCode)     { this.qrCode = qrCode; }

    @Override
    public String toString() {
        return String.format("Ticket{ id=%-6s type=%-12s price=$%-7.2f avail=%d }",
                ticketID, type, price, availability);
    }
}
