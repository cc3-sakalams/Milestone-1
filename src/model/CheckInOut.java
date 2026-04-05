package model;

public class CheckInOut {
    private String reference;
    private String status;
    private String digitalKey;

    public CheckInOut(String reference) {
        this.reference  = reference;
        this.status     = "PENDING";
        this.digitalKey = "";
    }

    public boolean performSelfServiceCheckIn() {
        System.out.println("[CHECK-IN] Scanning reference: " + reference);
        if (!validateReference()) {
            System.out.println("[CHECK-IN] ❌ Invalid booking reference: " + reference);
            System.out.println("[CHECK-IN] ⚠️  Please approach a staff member for assistance.");
            return false;
        }
        updateAccessPermissions("GRANT");
        this.digitalKey = "DKEY-" + reference + "-" + System.currentTimeMillis();
        this.status     = "CHECKED_IN";
        System.out.println("[CHECK-IN] ✅ Self-service check-in complete!");
        System.out.println("[CHECK-IN] 🔑 Digital key issued: " + digitalKey);
        return true;
    }

    public boolean performSelfServiceCheckOut() {
        if (!status.equals("CHECKED_IN")) {
            System.out.println("[CHECK-OUT] ❌ Not currently checked in. Status: " + status);
            return false;
        }
        updateAccessPermissions("REVOKE");
        this.status = "CHECKED_OUT";
        System.out.println("[CHECK-OUT] ✅ Self-service check-out complete. Safe travels!");
        System.out.println("[CHECK-OUT]    Reference: " + reference);
        return true;
    }

    public boolean validateReference() {
        return reference != null && !reference.isBlank() && reference.startsWith("BK-");
    }

    public void updateAccessPermissions(String action) {
        System.out.println("[ACCESS] 🔐 Access permissions " + action + "ED for ref: " + reference);
    }


    public String getReference()              { return reference; }
    public String getStatus()                 { return status; }
    public String getDigitalKey()             { return digitalKey; }
    public void   setStatus(String status)    { this.status = status; }
    public void   setDigitalKey(String key)   { this.digitalKey = key; }

    @Override
    public String toString() {
        return "CheckInOut{ ref=" + reference + ", status=" + status + " }";
    }
}
