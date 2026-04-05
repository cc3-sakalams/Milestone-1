package model;

public class Admin extends User {

    public Admin(String userID, String email) {
        super(userID, email);
    }


    public Report generateReport(String reportID, String type) {
        System.out.println("[ADMIN] " + email + " is generating report: " + type);
        Report report = new Report(reportID, type);
        report.generate();
        return report;
    }
}
