package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Report {
    private String        reportID;
    private String        type;
    private String        content;
    private LocalDateTime generatedAt;

    public Report(String reportID, String type) {
        this.reportID = reportID;
        this.type     = type;
        this.content  = "";
    }

    public void generate() {
        this.generatedAt = LocalDateTime.now();
        this.content     = buildContent();
        System.out.println("[REPORT] ✅ Report Generated!");
        System.out.println("[REPORT]    ID        : " + reportID);
        System.out.println("[REPORT]    Type      : " + type);
        System.out.println("[REPORT]    Timestamp : " +
                generatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("[REPORT]    Data      : " + content);
    }

    public void exportPDF() {
        System.out.println("[REPORT] 📄 Exporting PDF → " + reportID + ".pdf ... Done.");
    }

    public void exportExcel() {
        System.out.println("[REPORT] 📊 Exporting Excel → " + reportID + ".xlsx ... Done.");
    }

    private String buildContent() {
        return switch (type.toUpperCase()) {
            case "SALES"         -> "Revenue: $128,450 | Tickets Sold: 3,200 | Avg Ticket: $40.14";
            case "OCCUPANCY"     -> "Rooms Occupied: 87% | Villas: 95% | Suites: 72%";
            case "VISITOR_STATS" -> "Daily Visitors: 5,400 | Peak Hour: 2PM | Top Ride: Roller Coaster";
            case "PERFORMANCE"   -> "Staff Rating: 4.7/5 | Ride Uptime: 98.2% | Incidents: 0";
            default              -> "Aggregated data for report type: " + type;
        };
    }


    public String        getReportID()                    { return reportID; }
    public String        getType()                        { return type; }
    public String        getContent()                     { return content; }
    public LocalDateTime getGeneratedAt()                 { return generatedAt; }
    public void          setContent(String content)       { this.content = content; }
    public void          setGeneratedAt(LocalDateTime dt) { this.generatedAt = dt; }

    @Override
    public String toString() {
        return String.format("Report{ id=%-12s type=%-15s generated=%s }",
                reportID, type, generatedAt != null ?
                        generatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A");
    }
}
