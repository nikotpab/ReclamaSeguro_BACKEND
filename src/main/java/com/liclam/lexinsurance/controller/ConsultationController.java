package com.liclam.lexinsurance.controller;

import com.liclam.lexinsurance.dto.ConsultationRequest;
import com.liclam.lexinsurance.dto.SignatureRequest;
import com.liclam.lexinsurance.entity.Consultation;
import com.liclam.lexinsurance.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    @Autowired private ConsultationService consultationService;

    @PostMapping("/create")
    public ResponseEntity<?> createConsultation(@RequestBody ConsultationRequest req) {
        try {
            Consultation created = consultationService.createConsultation(req);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/sign")
    public ResponseEntity<?> uploadSignature(@PathVariable Long id, @RequestBody SignatureRequest req) {
        try {
            consultationService.saveSignature(id, req.getBase64Signature());
            return ResponseEntity.ok("Firma guardada correctamente.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error guardando firma: " + e.getMessage());
        }
    }
}