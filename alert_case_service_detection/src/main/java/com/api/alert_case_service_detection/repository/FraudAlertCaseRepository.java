package com.api.alert_case_service_detection.repository;

import com.api.alert_case_service_detection.entity.FraudAlertCaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FraudAlertCaseRepository extends JpaRepository<FraudAlertCaseEntity, Long> {
    Optional<FraudAlertCaseEntity> findByTransactionId(String transactionId);
}
