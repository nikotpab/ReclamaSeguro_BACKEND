package com.liclam.lexinsurance.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminConsultationDto {
    private Long id;
    private String userName; 
    private String deceasedName; 
    private String docNumber;    
    private String status;
    private LocalDateTime createdAt;
    private String signatureBase64;
}