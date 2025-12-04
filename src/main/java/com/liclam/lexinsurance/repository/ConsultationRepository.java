package com.liclam.lexinsurance.repository;

import com.liclam.lexinsurance.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByUser_Id(Long userId);
}