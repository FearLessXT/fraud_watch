package com.api.alert_case_service_detection.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FraudAlertCaseEvent(
        @JsonProperty("eventType")
        String eventType,

        @JsonProperty("eventVersion")
        int eventVersion,

        @JsonProperty("transactionId")
        String transactionId,

        @JsonProperty("riskScore")
        int riskScore,

        @JsonProperty("riskLevel")
        String riskLevel,

        @JsonProperty("reasons")
        List<String> reasons,

        @JsonProperty("signals")
        Map<String, Object> signals
) {
    public static FraudAlertCaseEvent from(
            String transactionId,
            int riskScore,
            String riskLevel,
            List<String> reasons,
            Map<String, Object> signals
    ) {
        return new FraudAlertCaseEvent(
                "FraudAlertCase",
                1,
                transactionId,
                riskScore,
                riskLevel,
                reasons,
                signals
        );
    }
}