package com.api.enrichment_service_fraud.consumer;

import com.api.enrichment_service_fraud.dto.TransactionReceivedEvent;
import com.api.enrichment_service_fraud.event.TransactionEnrichedEvent;
import com.api.enrichment_service_fraud.service.GeoDeviceSignalService;
import com.api.enrichment_service_fraud.service.VelocityService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class TransactionRawListener {
    private static final Logger log =
            LoggerFactory.getLogger(TransactionRawListener.class);

    private static final String CORRELATION_ID = "X-Correlation-Id";
    private static final String ENRICHED_TOPIC = "transactions.enriched";

    private final ObjectMapper objectMapper;
    private final VelocityService velocityService;
    private final GeoDeviceSignalService geoDeviceSignalService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public TransactionRawListener(ObjectMapper objectMapper, VelocityService velocityService, GeoDeviceSignalService geoDeviceSignalService, KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.velocityService = velocityService;
        this.geoDeviceSignalService = geoDeviceSignalService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
            topics = "transactions.raw",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(
            ConsumerRecord<String, String> record) {

        try {
            // Correlation ID
            var header = record.headers().lastHeader(CORRELATION_ID);

            if (header != null) {
                MDC.put(
                        CORRELATION_ID,
                        new String(header.value(), StandardCharsets.UTF_8)
                );
            }

            // Explicit deserialization (SAFE)
            TransactionReceivedEvent event =
                    objectMapper.readValue(
                            record.value(),
                            TransactionReceivedEvent.class
                    );

            log.debug(
                    "Consumed transaction {} for enrichment",
                    event.transactionId()
            );

            // Enrichment logic
            var velocity =
                    velocityService.recordAndGetVelocity(
                            event.customerId(),
                            event.deviceId()
                    );

            log.debug(
                    "Velocity signals for txn={} => {}",
                    event.transactionId(),
                    velocity
            );

            var geoDeviceSignals =
                    geoDeviceSignalService.evaluate(
                            event.customerId(),
                            event.country(),
                            event.deviceId()
                    );

            log.debug(
                    "Enriched txn={} velocity={} geoDevice={}",
                    event.transactionId(),
                    velocity,
                    geoDeviceSignals
            );

            // Merge signals
            Map<String, Object> signals = new HashMap<>();
            signals.putAll(velocity);
            signals.putAll(geoDeviceSignals);

            TransactionEnrichedEvent enriched =
                    TransactionEnrichedEvent.from(event, signals);

            String payload =
                    objectMapper.writeValueAsString(enriched);

            ProducerRecord<String, String> out =
                    new ProducerRecord<>(
                            ENRICHED_TOPIC,
                            enriched.transactionId(),
                            payload
                    );

            // Preserve correlation ID
            if (header != null) {
                out.headers().add(header);
            }

            kafkaTemplate.send(out).get(); // sync ACK

            log.debug(
                    "Published enriched event txn={} signals={}",
                    enriched.transactionId(),
                    signals
            );
        } catch (Exception ex) {
            log.error(
                    "Failed to process record at offset={}",
                    record.offset(),
                    ex
            );
            // No ACK → Kafka retry
        } finally {
            MDC.clear();
        }
    }
}
