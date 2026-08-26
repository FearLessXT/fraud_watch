package com.api.alert_case_service_detection.consumer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class TransactionEnrichedListener {

    private static final String CID = "X-Correlation-Id";

    private final ObjectMapper mapper;

    @KafkaListener(
            topics = "transactions.enriched",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(ConsumerRecord<String,String> record, Acknowledgment ack) {
        try {
            var h = record.headers().lastHeader(CID);
            if (h != null) MDC.put(CID, new String(h.value(), StandardCharsets.UTF_8));

            var json = mapper.readTree(record.value());

            int riskScore = json.get("riskScore").asInt();
            String riskLevel =
                    riskScore >= 70 ? "HIGH" :
                            riskScore >= 30 ? "MEDIUM" : "LOW";

            // Process the enriched transaction with the calculated risk level
            // TODO: Add business logic for handling enriched transactions

            ack.acknowledge();
        } catch (Exception e) {
            // retry
        } finally {
            MDC.clear();
        }
    }
}