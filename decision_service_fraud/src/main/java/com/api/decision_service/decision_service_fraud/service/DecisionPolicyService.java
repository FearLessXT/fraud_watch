package com.api.decision_service.decision_service_fraud.service;

import org.springframework.stereotype.Service;

@Service
public class DecisionPolicyService {

    public String decide(String riskLevel, int riskScore) {
        return switch (riskLevel) {
            case
                "HIGH" -> "BLOCK";
            case "MEDIUM" -> "REVIEW";

            default -> "ALLOW";
        };
    }
}
