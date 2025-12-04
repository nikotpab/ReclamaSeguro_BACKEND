package com.liclam.lexinsurance.service;

import com.liclam.lexinsurance.dto.ConsultationRequest;
import com.liclam.lexinsurance.entity.Consultation;
import com.liclam.lexinsurance.entity.User;
import com.liclam.lexinsurance.repository.ConsultationRepository;
import com.liclam.lexinsurance.repository.UserRepository;
import com.liclam.lexinsurance.util.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class ConsultationService {

    @Autowired private ConsultationRepository consultationRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private CryptoService cryptoService;

    @Value("${app.storage.location}")
    private String uploadDir;

    public Consultation createConsultation(ConsultationRequest req) {
        User user = userRepo.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Consultation cons = new Consultation();
        cons.setUser(user);
        cons.setConsultationType(req.getType());
        cons.setDocType(req.getDocType());
        cons.setDeathDate(req.getDeathDate());
        cons.setKinship(req.getKinship());
        
        if (req.getDeceasedName() != null) {
            cons.setEncryptedDeceasedName(cryptoService.encrypt(req.getDeceasedName()));
        }
        if (req.getDocNumber() != null) {
            cons.setEncryptedDocNumber(cryptoService.encrypt(req.getDocNumber()));
        }

        return consultationRepo.save(cons);
    }

    public Consultation getConsultation(Long id) {
        return consultationRepo.findById(id).orElseThrow(() -> new RuntimeException("No encontrado"));
    }

    public void saveSignature(Long consultationId, String base64Signature) {
        processSignature(consultationId, base64Signature, false);
    }

    public void processPayment(Long id) {
    Consultation consultation = consultationRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));
    consultation.setPaymentApproved(true);
    consultation.setStatus("PAID");
    consultationRepo.save(consultation);
    }
    
    public void saveMandateSignature(Long consultationId, String base64Signature) {
        processSignature(consultationId, base64Signature, true);
    }

    private void processSignature(Long consultationId, String base64Signature, boolean isMandate) {
        Consultation consultation = consultationRepo.findById(consultationId)
                .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));

        String base64Image = base64Signature.contains(",") ? base64Signature.split(",")[1] : base64Signature;
        byte[] rawImageBytes = Base64.getDecoder().decode(base64Image);
        byte[] compressedBytes = cryptoService.compress(rawImageBytes);
        byte[] encryptedBytes = cryptoService.encryptBytes(compressedBytes);

        if (isMandate) {
            consultation.setMandateSigned(true);
            consultation.setMandateSignatureData(encryptedBytes);
            consultation.setMandateSignatureTimestamp(LocalDateTime.now());
            consultation.setStatus("CLAIM_STARTED");
        } else {
            consultation.setAuthorizationSigned(true);
            consultation.setSignatureData(encryptedBytes);
            consultation.setSignatureTimestamp(LocalDateTime.now());
        }
        
        consultationRepo.save(consultation);
    }

    public void uploadDocument(Long consultationId, String docType, MultipartFile file) throws IOException {
        Consultation consultation = consultationRepo.findById(consultationId)
                .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));

        Path uploadPath = Paths.get(uploadDir + "/docs");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = consultationId + "_" + docType + "_" + UUID.randomUUID() + ".pdf"; // Asumiendo PDF o IMG
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        switch (docType) {
            case "cedula": consultation.setDocCedulaPath(filePath.toString()); break;
            case "defuncion": consultation.setDocDefuncionPath(filePath.toString()); break;
            case "parentesco": consultation.setDocParentescoPath(filePath.toString()); break;
        }

        consultationRepo.save(consultation);
    }
}