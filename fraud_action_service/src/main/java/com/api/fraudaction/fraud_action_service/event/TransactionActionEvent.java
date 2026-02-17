package com.api.fraudaction.fraud_action_service.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TransactionActionEvent(

        @JsonProperty("eventType")
        String eventType,

        @JsonProperty("eventVersion")
        int eventVersion,

        @JsonProperty("transactionId")
        String transactionId,

        @JsonProperty("decision")
        String decision,

        @JsonProperty("action")
        String action,

        @JsonProperty("actionAt")
        Instant actionAt
) {
    public static TransactionActionEvent from(
            String trnId,
            String decision,
            String action
    ) {
        return new TransactionActionEvent(
                "TransactionActioned",
                1,
                trnId,
                decision,
                action,
                Instant.now()
        );
    }
}
