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
@RequiredArgsConstructor
public class TransactionDecisionListener {

    private static final String CORRELATION_ID = "X-Correlation-ID";
    private static final String ACTION_TOPIC = "transactions.actioned";

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ActionExecutorService actionExecutorService;

    @KafkaListener(
            topics = "transactions.decisioned",
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
            var transactionId = json.get("transactionId").asString();
            var decision = json.get("decision").asString();

            String action = actionExecutorService.executeAction(transactionId, decision);
            var actionEvent = TransactionActionEvent.from(
                    transactionId,
                    decision,
                    action
            );

            String payload = objectMapper.writeValueAsString(actionEvent);

            ProducerRecord<String, String> actionRecord = new ProducerRecord<>(
                    ACTION_TOPIC,
                    transactionId,
                    payload
            );

            if (header != null) {
                actionRecord.headers().add(header);
            }

            kafkaTemplate.send(actionRecord).get();
            acknowledgment.acknowledge();
        } catch (Exception e) {
            // kafka retry
        } finally {
            MDC.clear();
        }
    }
}
