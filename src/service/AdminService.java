package service;

import java.util.List;
import java.util.Optional;
import model.Admin;
import model.Report;
import model.User;
import repository.ReportRepository;
import repository.UserRepository;


public class AdminService {

    private final UserRepository   userRepo;
    private final ReportRepository reportRepo;

    public AdminService(UserRepository userRepo, ReportRepository reportRepo) {
        this.userRepo   = userRepo;
        this.reportRepo = reportRepo;
    }


    public Admin registerAdmin(String userID, String email) {
        Optional<User> existing = userRepo.findByEmail(email);
        if (existing.isPresent()) {
            System.out.println("[ADMIN SERVICE] ❌ Email already in use: " + email);
            return null;
        }
        Admin admin = new Admin(userID, email);
        admin.register();
        admin.issueSessionToken();
        userRepo.save(admin);
        System.out.println("[ADMIN SERVICE] ✅ Admin saved to DB.\n");
        return admin;
    }


    public Report generateReport(Admin admin, String type) {
        String reportID = "RPT-" + type.toUpperCase() + "-" + System.currentTimeMillis() % 100000;
        Report report   = admin.generateReport(reportID, type.toUpperCase());
        reportRepo.save(report);
        System.out.println("[ADMIN SERVICE] ✅ Report saved to DB.");
        report.exportPDF();
        report.exportExcel();
        System.out.println();
        return report;
    }


    public List<Report> getAllReports()     { return reportRepo.findAll(); }
    public List<User>   getAllUsers()       { return userRepo.findAll(); }
}
