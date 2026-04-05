package model;

import java.util.ArrayList;
import java.util.List;

public class Membership {
    private String membershipID;
    private String userID;
    private String type;
    private List<String> perks;
    private boolean active;

    public Membership(String membershipID, String userID, String type) {
        this.membershipID = membershipID;
        this.userID = userID;
        this.type = type;
        this.perks = new ArrayList<>();
        this.active = false;
    }

    public void purchase() {
        this.active = true;
        System.out.println("[MEMBERSHIP] ✅ Membership activated: " + type);
        System.out.println("[MEMBERSHIP]    Perks: " + (perks.isEmpty() ? "None" : String.join(", ", perks)));
    }

    public void renew() {
        this.active = true;
        System.out.println("[MEMBERSHIP] ✅ Membership renewed: " + type);
    }

    public void addPerk(String perk) {
        perks.add(perk);
    }

    public String getMembershipID() { return membershipID; }
    public String getUserID() { return userID; }
    public String getType() { return type; }
    public List<String> getPerks() { return perks; }
    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }
    public void setPerks(List<String> perks) { this.perks = perks; }

    @Override
    public String toString() {
        return String.format("Membership{ id=%-8s type=%-12s active=%s perks=%s }",
                membershipID, type, active ? "YES" : "NO", perks);
    }
}
