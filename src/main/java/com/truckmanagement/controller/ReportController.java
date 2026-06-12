package com.truckmanagement.controller;

import com.truckmanagement.model.Trip;
import com.truckmanagement.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReportController {

    @Autowired private TripService tripService;
    @Autowired private ReportService reportService;
    @Autowired private SettingsService settingsService;

    @GetMapping("/report")
    public ResponseEntity<byte[]> downloadReport(
            @RequestParam(defaultValue = "full")  String report_type,
            @RequestParam(defaultValue = "All")   String truck,
            @RequestParam(defaultValue = "All")   String driver,
            @RequestParam(defaultValue = "")      String date_from,
            @RequestParam(defaultValue = "")      String date_to) throws Exception {

        List<Trip> trips = tripService.getFiltered("", truck, driver, date_from, date_to);
        String company   = settingsService.get().getCompanyName();
        byte[] data      = reportService.generate(trips, report_type, company);

        String ts    = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fname = "report_" + report_type + "_" + ts + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename(fname).build());

        return ResponseEntity.ok().headers(headers).body(data);
    }

    @PostMapping("/backup")
    public ResponseEntity<?> backup() {
        try {
            java.io.File dataDir = new java.io.File("data/backups");
            dataDir.mkdirs();
            java.io.File db = new java.io.File("data/truckdb.mv.db");
            if (!db.exists()) return ResponseEntity.ok().body(java.util.Map.of("message","No database found"));
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            java.io.File dest = new java.io.File("data/backups/backup_" + ts + ".mv.db");
            java.nio.file.Files.copy(db.toPath(), dest.toPath());
            return ResponseEntity.ok().body(java.util.Map.of("message", "Backup saved: " + dest.getName()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/dashboard")
    public java.util.Map<String, Object> dashboard() {
        return tripService.getDashboard();
    }
}
