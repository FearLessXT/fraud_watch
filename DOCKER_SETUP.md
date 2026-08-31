# Docker Deployment Guide

This guide explains how to deploy the Fraud Detection Platform using Docker and Docker Compose.

## Prerequisites
- Docker installed (version 20.10+)
- Docker Compose installed (version 2.0+)
- At least 4GB RAM available for Docker
- Port availability: 2181, 3306, 6379, 8888, 9092, 9093

## Architecture Overview

The docker-compose.yml orchestrates the following services:

1. **Zookeeper** - Required for Kafka coordination
2. **Kafka** - Message broker for event streaming
3. **Redis** - Caching for enrichment service
4. **MySQL** - Database for persistent storage
5. **Config Server** - Centralized configuration management

## Quick Start

### 1. Start Infrastructure Services
```bash
# Start all infrastructure services
docker-compose up -d

# Check service status
docker-compose ps

# View logs
docker-compose logs -f
```

### 2. Initialize Kafka Topics
```bash
# Use the kafka manager script
cd kafka_service
./kafka-manager.sh init
```

### 3. Start Application Services
After infrastructure is running, start your Spring Boot services:
```bash
# Terminal 1 - Config Server
cd config-server
./gradlew bootRun

# Terminal 2 - Auth Service
cd auth_service_fraud
./gradlew bootRun

# Repeat for other services...
```

## Service Details

### Kafka Service
- **Internal Port**: 9092 (service-to-service)
- **External Port**: 9093 (client access)
- **Topics**: Auto-created or use `kafka-manager.sh init`
- **Persistence**: Docker volume `kafka_data`

### Redis Service
- **Port**: 6379
- **Persistence**: Docker volume `redis_data`
- **Usage**: Velocity calculations and caching

### MySQL Service
- **Port**: 3306
- **Databases**: fraud_detection, auth_service
- **Credentials**: fraud_user/fraud_password
- **Persistence**: Docker volume `mysql_data`

### Config Server
- **Port**: 8888
- **Configuration**: Git-based backend
- **Path**: `~/fraud_detection_config_repo`

## Management Commands

### Kafka Management
```bash
cd kafka_service

# List topics
./kafka-manager.sh list

# Create topic
./kafka-manager.sh create-topic transactions.raw 3 1

# Consume from topic
./kafka-manager.sh consume transactions.raw

# Produce to topic
./kafka-manager.sh produce transactions.raw

# Describe topic
./kafka-manager.sh describe-topic transactions.raw
```

### Docker Compose Commands
```bash
# Start all services
docker-compose up -d

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Restart specific service
docker-compose restart kafka

# View service logs
docker-compose logs -f kafka

# Scale service (for Kafka cluster)
docker-compose up -d --scale kafka=3
```

## Production Considerations

### Security
- Change default passwords in docker-compose.yml
- Enable SSL/TLS for Kafka
- Configure network policies
- Use secrets management

### High Availability
- Run Kafka in cluster mode (multiple brokers)
- Add Zookeeper ensemble (3+ nodes)
- Configure MySQL replication
- Add Redis clustering

### Monitoring
- Add Prometheus exporters
- Configure Grafana dashboards
- Enable Kafka JMX metrics
- Set up log aggregation

### Performance
- Adjust Kafka partition counts
- Tune JVM settings for services
- Configure connection pooling
- Optimize Redis caching strategies

## Troubleshooting

### Kafka won't start
```bash
# Check Zookeeper is running
docker-compose logs zookeeper

# Check Kafka logs
docker-compose logs kafka

# Verify ports are available
netstat -an | grep 9092
```

### Connection refused
```bash
# Check if services are running
docker-compose ps

# Verify network connectivity
docker network inspect fraud_detection_fraud_detection_network

# Check service health
docker-compose ps
```

### Data persistence issues
```bash
# Check volume status
docker volume ls

# Inspect volume
docker volume inspect fraud_detection_kafka_data

# Backup volumes
docker run --rm -v fraud_detection_kafka_data:/data -v $(pwd):/backup alpine tar czf /backup/kafka-backup.tar.gz /data
```

## Backup and Recovery

### Backup Kafka Data
```bash
# Backup Kafka volume
docker run --rm -v fraud_detection_kafka_data:/data -v $(pwd):/backup alpine tar czf /backup/kafka-$(date +%Y%m%d).tar.gz /data
```

### Backup MySQL Data
```bash
# Backup MySQL database
docker exec fraud_detection_mysql mysqldump -u fraud_user -pfraud_password fraud_detection > fraud_detection_backup.sql
```

### Restore MySQL Data
```bash
# Restore MySQL database
docker exec -i fraud_detection_mysql mysql -u fraud_user -pfraud_password fraud_detection < fraud_detection_backup.sql
```

## Network Configuration

Services communicate via the `fraud_detection_network` bridge network. Internal service names:
- `zookeeper:2181` - Zookeeper
- `kafka:9092` - Kafka (internal)
- `localhost:9093` - Kafka (external)
- `redis:6379` - Redis
- `mysql:3306` - MySQL
- `config-server:8888` - Config Server

## Configuration Updates

To update service configurations:
1. Modify environment variables in docker-compose.yml
2. Update configuration files (server.properties, etc.)
3. Restart affected services
4. `docker-compose up -d <service-name>`

## Clean Up

```bash
# Stop all services and remove containers
docker-compose down

# Remove all volumes (WARNING: deletes data)
docker-compose down -v

# Remove everything including networks
docker-compose down -v --remove-orphans
```