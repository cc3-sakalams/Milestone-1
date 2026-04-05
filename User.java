package model;

public abstract class User {
    protected String userID;
    protected String email;
    protected String sessionToken;

    public User(String userID, String email) {
        this.userID = userID;
        this.email  = email;
        this.sessionToken = "";
    }

    public void register() {
        System.out.println("[AUTH] Registered user: " + email);
        System.out.println("[AUTH] Welcome email sent to: " + email);
    }

    public boolean login(String email, String password) {
        // In a real system, hash + compare password from DB
        System.out.println("[AUTH] Login successful for: " + email);
        return true;
    }

    public String issueSessionToken() {
        this.sessionToken = "TKN-" + userID + "-" + System.currentTimeMillis();
        System.out.println("[AUTH] Session token issued: " + sessionToken);
        return this.sessionToken;
    }

    // Getters & Setters
    public String getUserID()                          { return userID; }
    public String getEmail()                           { return email; }
    public String getSessionToken()                    { return sessionToken; }
    public void   setSessionToken(String sessionToken) { this.sessionToken = sessionToken; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{ id=" + userID + ", email=" + email + " }";
    }
}
