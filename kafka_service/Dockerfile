# Kafka Service Dockerfile for Fraud Detection Platform
FROM confluentinc/cp-kafka:7.5.0

# Set working directory
WORKDIR /etc/kafka

# Copy custom Kafka configuration
COPY server.properties /etc/kafka/server.properties

# Create directories for Kafka data
RUN mkdir -p /var/lib/kafka/data && \
    chown -R appuser:appuser /var/lib/kafka/data

# Expose Kafka ports
# 9092: Internal Kafka broker port
# 9093: External Kafka broker port
# 9094: Schema Registry port (if needed)
EXPOSE 9092 9093

# Set environment variables
ENV KAFKA_BROKER_ID=1
ENV KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181
ENV KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:9093
ENV KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
ENV KAFKA_INTER_BROKER_LISTENER_NAME=PLAINTEXT
ENV KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1
ENV KAFKA_AUTO_CREATE_TOPICS_ENABLE=true

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD kafka-broker-api-versions --bootstrap-server localhost:9092 || exit 1

# Start Kafka broker
CMD ["sh", "-c", "kafka-server-start /etc/kafka/server.properties"]