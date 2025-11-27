package com.liclam.lexinsurance.entity;

import jakarta.persistence.*;
import lombok.Data;
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
    private String signatureFilePath;
    private LocalDateTime signatureTimestamp;

    private String status; 
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = "IN_PROGRESS";
    }
}