#!/bin/bash

# Kafka Management Script for Fraud Detection Platform
# This script provides common Kafka operations

KAFKA_CONTAINER="fraud_detection_kafka"
BOOTSTRAP_SERVER="localhost:9092"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to check if Kafka container is running
check_kafka_running() {
    if ! docker ps | grep -q $KAFKA_CONTAINER; then
        echo -e "${RED}Error: Kafka container is not running${NC}"
        echo "Start it with: docker-compose up -d kafka"
        exit 1
    fi
}

# Function to list all topics
list_topics() {
    echo -e "${GREEN}Listing all Kafka topics:${NC}"
    docker exec -it $KAFKA_CONTAINER kafka-topics --list --bootstrap-server $BOOTSTRAP_SERVER
}

# Function to create a topic
create_topic() {
    if [ -z "$1" ]; then
        echo -e "${RED}Error: Topic name required${NC}"
        echo "Usage: ./kafka-manager.sh create-topic <topic-name> [partitions] [replication-factor]"
        exit 1
    fi
    
    local topic=$1
    local partitions=${2:-3}
    local replication=${3:-1}
    
    echo -e "${GREEN}Creating topic: $topic with $partitions partitions and replication factor $replication${NC}"
    docker exec -it $KAFKA_CONTAINER kafka-topics --create \
        --topic $topic \
        --bootstrap-server $BOOTSTRAP_SERVER \
        --partitions $partitions \
        --replication-factor $replication
}

# Function to delete a topic
delete_topic() {
    if [ -z "$1" ]; then
        echo -e "${RED}Error: Topic name required${NC}"
        echo "Usage: ./kafka-manager.sh delete-topic <topic-name>"
        exit 1
    fi
    
    local topic=$1
    echo -e "${YELLOW}Deleting topic: $topic${NC}"
    docker exec -it $KAFKA_CONTAINER kafka-topics --delete \
        --topic $topic \
        --bootstrap-server $BOOTSTRAP_SERVER
}

# Function to describe a topic
describe_topic() {
    if [ -z "$1" ]; then
        echo -e "${RED}Error: Topic name required${NC}"
        echo "Usage: ./kafka-manager.sh describe-topic <topic-name>"
        exit 1
    fi
    
    local topic=$1
    echo -e "${GREEN}Describing topic: $topic${NC}"
    docker exec -it $KAFKA_CONTAINER kafka-topics --describe \
        --topic $topic \
        --bootstrap-server $BOOTSTRAP_SERVER
}

# Function to consume from a topic
consume_topic() {
    if [ -z "$1" ]; then
        echo -e "${RED}Error: Topic name required${NC}"
        echo "Usage: ./kafka-manager.sh consume <topic-name> [from-beginning]"
        exit 1
    fi
    
    local topic=$1
    local from_beginning=${2:-"--from-beginning"}
    
    echo -e "${GREEN}Consuming from topic: $topic${NC}"
    docker exec -it $KAFKA_CONTAINER kafka-console-consumer \
        --bootstrap-server $BOOTSTRAP_SERVER \
        --topic $topic \
        $from_beginning \
        --property print.key=true \
        --property key.separator=": "
}

# Function to produce to a topic
produce_topic() {
    if [ -z "$1" ]; then
        echo -e "${RED}Error: Topic name required${NC}"
        echo "Usage: ./kafka-manager.sh produce <topic-name>"
        exit 1
    fi
    
    local topic=$1
    echo -e "${GREEN}Producing to topic: $topic${NC}"
    echo "Enter your messages (Ctrl+C to exit):"
    docker exec -it $KAFKA_CONTAINER kafka-console-producer \
        --bootstrap-server $BOOTSTRAP_SERVER \
        --topic $topic \
        --property "parse.key=true" \
        --property "key.separator=:"
}

# Function to show consumer groups
consumer_groups() {
    echo -e "${GREEN}Listing consumer groups:${NC}"
    docker exec -it $KAFKA_CONTAINER kafka-consumer-groups \
        --bootstrap-server $BOOTSTRAP_SERVER \
        --list
}

# Function to describe consumer group
describe_consumer_group() {
    if [ -z "$1" ]; then
        echo -e "${RED}Error: Consumer group ID required${NC}"
        echo "Usage: ./kafka-manager.sh describe-group <consumer-group-id>"
        exit 1
    fi
    
    local group_id=$1
    echo -e "${GREEN}Describing consumer group: $group_id${NC}"
    docker exec -it $KAFKA_CONTAINER kafka-consumer-groups \
        --bootstrap-server $BOOTSTRAP_SERVER \
        --group $group_id \
        --describe
}

# Function to show broker info
broker_info() {
    echo -e "${GREEN}Kafka Broker Information:${NC}"
    docker exec -it $KAFKA_CONTAINER kafka-broker-api-versions \
        --bootstrap-server $BOOTSTRAP_SERVER
}

# Function to initialize fraud detection topics
init_topics() {
    echo -e "${GREEN}Initializing Fraud Detection Platform topics...${NC}"
    
    local topics=(
        "transactions.raw"
        "transactions.enriched" 
        "transactions.scored"
        "transactions.decisioned"
        "transactions.actioned"
    )
    
    for topic in "${topics[@]}"; do
        echo "Creating topic: $topic"
        docker exec -it $KAFKA_CONTAINER kafka-topics --create \
            --topic $topic \
            --bootstrap-server $BOOTSTRAP_SERVER \
            --partitions 3 \
            --replication-factor 1 2>/dev/null || echo "Topic $topic may already exist"
    done
    
    echo -e "${GREEN}Topics initialized successfully!${NC}"
}

# Main script logic
case "$1" in
    list)
        check_kafka_running
        list_topics
        ;;
    create-topic)
        check_kafka_running
        create_topic "$2" "$3" "$4"
        ;;
    delete-topic)
        check_kafka_running
        delete_topic "$2"
        ;;
    describe-topic)
        check_kafka_running
        describe_topic "$2"
        ;;
    consume)
        check_kafka_running
        consume_topic "$2" "$3"
        ;;
    produce)
        check_kafka_running
        produce_topic "$2"
        ;;
    groups)
        check_kafka_running
        consumer_groups
        ;;
    describe-group)
        check_kafka_running
        describe_consumer_group "$2"
        ;;
    broker-info)
        check_kafka_running
        broker_info
        ;;
    init)
        check_kafka_running
        init_topics
        ;;
    *)
        echo "Kafka Management Script for Fraud Detection Platform"
        echo ""
        echo "Usage: $0 {command}"
        echo ""
        echo "Commands:"
        echo "  list                    - List all topics"
        echo "  create-topic <name>     - Create a new topic (optional: partitions replication)"
        echo "  delete-topic <name>     - Delete a topic"
        echo "  describe-topic <name>   - Describe topic details"
        echo "  consume <topic>         - Consume messages from topic"
        echo "  produce <topic>         - Produce messages to topic"
        echo "  groups                  - List consumer groups"
        echo "  describe-group <id>     - Describe consumer group details"
        echo "  broker-info             - Show broker information"
        echo "  init                    - Initialize fraud detection topics"
        echo ""
        echo "Examples:"
        echo "  $0 list"
        echo "  $0 create-topic transactions.raw 3 1"
        echo "  $0 consume transactions.raw"
        echo "  $0 init"
        exit 1
        ;;
esac