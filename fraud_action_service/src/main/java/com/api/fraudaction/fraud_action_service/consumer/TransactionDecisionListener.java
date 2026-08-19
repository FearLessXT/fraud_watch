package com.api.fraudaction.fraud_action_service.consumer;

import com.api.fraudaction.fraud_action_service.event.TransactionActionEvent;
import com.api.fraudaction.fraud_action_service.service.ActionExecutorService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Component
@SuppressWarnings("all")
@RequiredArgsConstructor
public class TransactionDecisionListener {
    private static final String CORRELATION_ID = "X-Correlation-Id";
    private static final String ACTION_TOPIC = "transactions.actioned";

    private final ObjectMapper objectMapper;
    private final ActionExecutorService executor;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(
            topics = "transactions.dicisioned",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(
            ConsumerRecord<String, String> record,
            Acknowledgment acknowledgment
    ) {
        try {
            var header = record.headers().lastHeader(CORRELATION_ID);
            if (header != null) {
                MDC.put(
                        CORRELATION_ID,
                        new String(header.value(), StandardCharsets.UTF_8)
                );
            }

            var json = objectMapper.readTree(record.value());

            String trnId = json.get("transactionId").asText();
            String decision = json.get("decision").asText();

            String action = executor.execute(trnId, decision);

            var actionEvent = TransactionActionEvent
                    .from(trnId, decision, action);

            String payload = objectMapper.writeValueAsString(actionEvent);

            ProducerRecord<String, String> out = new ProducerRecord<>(ACTION_TOPIC, trnId, payload);

            if (header != null) {
                out.headers().add(header);
            }

            kafkaTemplate.send(out);
        } catch (Exception e) {
            // NO acknowledgement, kafaka will retry
        } finally {
            MDC.clear();
        }
    }
}
