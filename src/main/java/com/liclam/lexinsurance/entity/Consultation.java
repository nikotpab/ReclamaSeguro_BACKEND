package com.liclam.lexinsurance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "consultations")
public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String consultationType; 

    @Column(columnDefinition = "TEXT")
    private String encryptedDeceasedName;
    
    @Column(columnDefinition = "TEXT")
    private String encryptedDocNumber;
    
    private String docType;
    private String deathDate;
    private String kinship;

    private boolean authorizationSigned;
    
    @Column(name = "signature_data")
    private byte[] signatureData;
    
    private LocalDateTime signatureTimestamp;

    private boolean paymentApproved;
    
    private boolean mandateSigned;
    
    @Column(name = "mandate_signature_data")
    private byte[] mandateSignatureData;
    
    private LocalDateTime mandateSignatureTimestamp;

    private String docCedulaPath;
    private String docDefuncionPath;
    private String docParentescoPath;

    private BigDecimal liquidationGrossValue;
    private BigDecimal liquidationCommission;
    private BigDecimal liquidationNetValue;
    private LocalDate liquidationDate;

    private String status; 
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = "IN_PROGRESS";
        }
    }
}