package com.api.fraudaction.fraud_action_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ActionExecutorService {

    private static final Logger log = LoggerFactory.getLogger(ActionExecutorService.class);

    public String executeAction(
            String transactionId,
            String decision
    ) {
        return switch (decision) {
            case "ALLOW" -> {
                log.info("Transaction {} approved.", transactionId);
                yield "APPROVED";
            }
            case "REVIEW" -> {
                log.info("Transaction {} sent to review.", transactionId);
                yield "SENT_FOR_REVIEW";
            }
            case "BLOCK" -> {
                log.info("Transaction {} blocked.", transactionId);
                yield "BLOCKED";
            }
            default -> {
                log.info("Transaction {} unknown.", transactionId);
                yield "UNKNOWN";
            }
        };
    }
}
