package com.liclam.lexinsurance.service;

import com.liclam.lexinsurance.dto.ConsultationRequest;
import com.liclam.lexinsurance.entity.Consultation;
import com.liclam.lexinsurance.entity.User;
import com.liclam.lexinsurance.repository.ConsultationRepository;
import com.liclam.lexinsurance.repository.UserRepository;
import com.liclam.lexinsurance.util.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class ConsultationService {

    @Autowired private ConsultationRepository consultationRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private CryptoService cryptoService;

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

    public void saveSignature(Long consultationId, String base64Signature) {
        Consultation consultation = consultationRepo.findById(consultationId)
                .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));

        String base64Image = base64Signature;
        if (base64Signature.contains(",")) {
            base64Image = base64Signature.split(",")[1];
        }

        byte[] rawImageBytes = Base64.getDecoder().decode(base64Image);
        
        byte[] compressedBytes = cryptoService.compress(rawImageBytes);
        
        byte[] encryptedBytes = cryptoService.encryptBytes(compressedBytes);

        consultation.setAuthorizationSigned(true);
        consultation.setSignatureData(encryptedBytes);
        consultation.setSignatureTimestamp(LocalDateTime.now());
        
        consultationRepo.save(consultation);
    }
    
    public String getSignatureImage(Long consultationId) {
        Consultation consultation = consultationRepo.findById(consultationId)
                .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));
        
        if (consultation.getSignatureData() == null) return null;

        byte[] decryptedBytes = cryptoService.decryptBytes(consultation.getSignatureData());
        
        byte[] decompressedBytes = cryptoService.decompress(decryptedBytes);
        
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(decompressedBytes);
    }
}