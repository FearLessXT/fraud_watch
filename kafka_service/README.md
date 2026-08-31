# Kafka Service

This service provides the Kafka message broker for the Fraud Detection Platform.

## Purpose
Kafka serves as the central event streaming backbone, enabling:
- Real-time event propagation between microservices
- Event replay and auditing capabilities
- Decoupled service communication
- High-throughput message processing

## Architecture
- **Broker**: Single-node Kafka broker (can be scaled to cluster)
- **Topics**: Pre-configured topics for fraud detection events
- **Replication**: Single replica (dev environment)
- **Persistence**: 7-day log retention

## Topics Used
- `transactions.raw` - Raw transaction events from ingestion
- `transactions.enriched` - Enriched transaction data with fraud signals
- `transactions.scored` - Risk-scored transactions
- `transactions.decisioned` - Business policy decisions
- `transactions.actioned` - Final action outcomes

## Configuration
- **Internal Port**: 9092 (service-to-service communication)
- **External Port**: 9093 (client access)
- **Storage**: Docker volume for persistent data
- **Zookeeper**: Required for broker coordination

## Deployment
```bash
# Build and start Kafka service
docker-compose up -d kafka

# View logs
docker-compose logs -f kafka

# Stop service
docker-compose down kafka
```

## Management
```bash
# List topics
docker exec -it fraud_detection_kafka kafka-topics --list --bootstrap-server localhost:9092

# Create topic
docker exec -it fraud_detection_kafka kafka-topics --create --topic transactions.raw --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

# Consume from topic
docker exec -it fraud_detection_kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic transactions.raw --from-beginning

# Produce to topic
docker exec -it fraud_detection_kafka kafka-console-producer --bootstrap-server localhost:9092 --topic transactions.raw
```

## Monitoring
- Health check endpoint configured
- Metrics available via JMX
- Logs accessible via docker-compose logs

## Scaling
For production deployment:
1. Increase broker count
2. Update replication factor
3. Add multiple partitions
4. Configure multiple Zookeeper nodes
5. Add Kafka Connect for external system integration