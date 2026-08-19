package com.api.decision_service.decision_service_fraud.consumer;

import com.api.decision_service.decision_service_fraud.event.TransactionDecisionEvent;
import com.api.decision_service.decision_service_fraud.service.DecisionPolicyService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.AcknowledgeType;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
@SuppressWarnings("all")
public class TransactionScoredListener {
    private static final String CORRELATION_ID = "X-Correlation-Id";
    private static final String DECISION_TOPIC = "transactions.decisioned";

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final DecisionPolicyService decisionPolicyService;

    public void onMessage(
            ConsumerRecord<String, String> recored,
            AcknowledgeType ack
    ) {
        try {
            var header = recored.headers().lastHeader(CORRELATION_ID);
            if (header != null) {
                MDC.put(
                        CORRELATION_ID,
                        new String(header.value(), StandardCharsets.UTF_8)
                );
            }

            var json = objectMapper.readTree(recored.value());

            String trxId = json.get("transactionId").asString();
            int score = json.get("score").asInt();
            String level = json.get("level").asString();

            List<String> reasons = objectMapper.convertValue(json.get("reasons"), List.class);

            String decision = decisionPolicyService.decide(level, score);

            var decisionEvent =
                    TransactionDecisionEvent.from(
                            trxId,
                            decision,
                            score,
                            level,
                            reasons
                    );

            String payload =
                    objectMapper.writeValueAsString(decisionEvent);

            ProducerRecord<String, String> out =
                    new ProducerRecord<>(
                            DECISION_TOPIC,
                            trxId,
                            payload
                    );

            kafkaTemplate.send(out);
        } catch (Exception e) {
            // No ack, retry
        } finally {
            MDC.clear();
        }
    }
}
