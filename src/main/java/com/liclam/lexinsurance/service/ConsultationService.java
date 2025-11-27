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

import java.io.File;
import java.io.FileOutputStream;
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

    public void saveSignature(Long consultationId, String base64Signature) throws IOException {
        Consultation consultation = consultationRepo.findById(consultationId)
                .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String base64Image = base64Signature;
        if (base64Signature.contains(",")) {
            base64Image = base64Signature.split(",")[1];
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        String fileName = "firma_" + consultationId + "_" + UUID.randomUUID() + ".png";
        File file = new File(uploadDir + File.separator + fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(imageBytes);
        }

        consultation.setAuthorizationSigned(true);
        consultation.setSignatureFilePath(file.getAbsolutePath());
        consultation.setSignatureTimestamp(LocalDateTime.now());
        
        consultationRepo.save(consultation);
    }
}