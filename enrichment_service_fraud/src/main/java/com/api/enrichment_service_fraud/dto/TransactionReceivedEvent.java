package com.api.enrichment_service_fraud.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TransactionReceivedEvent(
        @JsonProperty("eventType")
        String eventType,

        @JsonProperty("eventVersion")
        int eventVersion,

        @JsonProperty("transactionId")
        String transactionId,

        @JsonProperty("customerId")
        String customerId,

        @JsonProperty("amount")
        BigDecimal amount,

        @JsonProperty("currency")
        String currency,

        @JsonProperty("country")
        String country,

        @JsonProperty("deviceId")
        String deviceId,

        @JsonProperty("timestamp")
        Instant timestamp
) {
}
