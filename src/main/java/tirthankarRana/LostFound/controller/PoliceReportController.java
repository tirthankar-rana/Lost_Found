package tirthankarRana.LostFound.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tirthankarRana.LostFound.dto.PoliceReportRequest;

@RestController
@RequestMapping("/api/police-reports")
public class PoliceReportController {

    @PostMapping
    public ResponseEntity<?> createReport(@RequestBody PoliceReportRequest request) {
        if (isBlank(request.getFullName()) || isBlank(request.getPhone()) || isBlank(request.getCity())
            || isBlank(request.getItemTitle()) || isBlank(request.getIncidentDate())
            || isBlank(request.getLocation()) || isBlank(request.getDetails())) {
            return ResponseEntity.badRequest().body(Map.of("error", "All report fields are required"));
        }

        String reportId = "LFR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String generatedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        String reportBody = "Police Complaint Draft\n"
            + "Report ID: " + reportId + "\n"
            + "Generated At: " + generatedAt + "\n\n"
            + "Name: " + request.getFullName() + "\n"
            + "Phone: " + request.getPhone() + "\n"
            + "City/Station: " + request.getCity() + "\n"
            + "Item: " + request.getItemTitle() + "\n"
            + "Incident Date: " + request.getIncidentDate() + "\n"
            + "Location: " + request.getLocation() + "\n\n"
            + "Details:\n" + request.getDetails() + "\n";

        return ResponseEntity.ok(
            Map.of(
                "message", "Report draft generated successfully",
                "reportId", reportId,
                "generatedAt", generatedAt,
                "reportText", reportBody
            )
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
