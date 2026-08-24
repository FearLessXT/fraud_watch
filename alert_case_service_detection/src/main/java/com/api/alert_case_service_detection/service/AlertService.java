package com.api.alert_case_service_detection.service;

import com.api.alert_case_service_detection.entity.FraudAlertCaseEntity;
import com.api.alert_case_service_detection.repository.FraudAlertCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlertService {
    private final FraudAlertCaseRepository fcaRepository;

    public void createCaseIfRequired(String txnId, String decision,
                                     Integer score, String level,
                                     String reasons, String correlationId) {

        if ("REVIEW".equals(decision) || "BLOCK".equals(decision)) {

            String priority = "BLOCK".equals(decision) ? "HIGH" : "MEDIUM";

            fcaRepository.findByTransactionId(txnId)
                    .orElseGet(() -> fcaRepository.save(
                            new FraudAlertCaseEntity(
                                    txnId,
                                    decision,
                                    priority,
                                    score,
                                    level,
                                    reasons,
                                    correlationId
                            )
                    ));
        }
    }
}
