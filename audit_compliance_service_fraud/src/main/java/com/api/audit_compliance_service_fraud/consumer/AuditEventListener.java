package com.api.audit_compliance_service_fraud.consumer;

import com.api.audit_compliance_service_fraud.entity.AuditEntry;
import com.api.audit_compliance_service_fraud.repository.AuditEntryRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Component
public class AuditEventListener {

    private static final String CORRELATION_ID = "X-Correlation-Id";

    private final ObjectMapper objectMapper;
    private final AuditEntryRepository repository;

    public AuditEventListener(
            ObjectMapper objectMapper,
            AuditEntryRepository repository) {
        this.objectMapper = objectMapper;
        this.repository = repository;
    }

    @KafkaListener(
            topics = {
                    "transactions.decisioned",
                    "transactions.actioned"
            },
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(
            ConsumerRecord<String, String> record) {

        try {
            var header = record.headers().lastHeader(CORRELATION_ID);
            if (header != null) {
                MDC.put(
                        CORRELATION_ID,
                        new String(header.value(), StandardCharsets.UTF_8)
                );
            }

            var json = objectMapper.readTree(record.value());

            AuditEntry entry =
                    new AuditEntry(
                            json.get("transactionId").asString(),
                            json.get("decision").asString(),
                            json.has("action") ? json.get("action").asString() : null,
                            json.has("riskScore") ? json.get("riskScore").asInt() : 0,
                            json.has("riskLevel") ? json.get("riskLevel").asString() : "NA",
                            json.has("reasons")
                                    ? objectMapper.writeValueAsString(json.get("reasons"))
                                    : null,
                            MDC.get(CORRELATION_ID),
                            json.get("eventType").asString(),
                            json.get("eventVersion").asInt()
                    );

            repository.save(entry);
        } catch (Exception ex) {
            // No ACK → retry
        } finally {
            MDC.clear();
        }
    }
}
