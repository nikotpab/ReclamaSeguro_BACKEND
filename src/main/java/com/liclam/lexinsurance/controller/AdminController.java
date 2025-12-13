package com.liclam.lexinsurance.controller;

import com.liclam.lexinsurance.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private ConsultationService consultationService;

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(consultationService.getStats());
    }

    @GetMapping("/consultations")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(consultationService.getAllConsultationsForAdmin(page, size));
    }

    @GetMapping("/consultations/{id}")
    public ResponseEntity<?> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(consultationService.getConsultationDetail(id));
    }

    @PutMapping("/consultations/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        consultationService.updateStatus(id, payload.get("status"));
        return ResponseEntity.ok(Map.of("message", "Estado actualizado"));
    }
}