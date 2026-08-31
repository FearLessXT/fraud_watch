package com.api.enrichment_service_fraud;

import org.junit.jupiter.api.Test;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.context.environment.EnvironmentManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class EnrichmentServiceFraudApplicationTests {

    private final KafkaProperties kafkaProperties;

    EnrichmentServiceFraudApplicationTests(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Test
    void kafkaPropertiesShouldBeLoaded() {

    }
}
