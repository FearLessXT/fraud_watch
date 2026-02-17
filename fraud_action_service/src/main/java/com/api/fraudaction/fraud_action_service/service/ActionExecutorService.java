package com.api.fraudaction.fraud_action_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ActionExecutorService {
    private static final Logger logger = LoggerFactory.getLogger(ActionExecutorService.class);

    public String execute(
            String transactionId,
            String decision
    ) {
        return switch (decision) {
            case "ALLOW" -> {
                logger.info("Transaction {} allowed", transactionId);
                yield "APPROVED";
            }
            case "REVIEW" -> {
                logger.info("Transaction {} reviewed", transactionId);
                yield "SENT_FOR_REVIEW";
            }
            case "BLOCK" -> {
                logger.info("Transaction {} blocked", transactionId);
                yield "REJECTED";
            }

            default -> {
                logger.error("Unknown decision for transaction {}", transactionId);
                yield "UNKNOWN";
            }
        };
    }
}
