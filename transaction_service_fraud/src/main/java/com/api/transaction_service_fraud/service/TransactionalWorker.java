package com.api.transaction_service_fraud.service;

import com.api.transaction_service_fraud.domain.OutboxEvent;
import com.api.transaction_service_fraud.domain.Transaction;
import com.api.transaction_service_fraud.dto.TransactionRequest;
import com.api.transaction_service_fraud.event.TransactionReceivedEvent;
import com.api.transaction_service_fraud.repository.OutboxRepository;
import com.api.transaction_service_fraud.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class TransactionalWorker {
    private final TransactionRepository txRepo;
    private final OutboxRepository outboxRepo;
    private final ObjectMapper mapper;

    public TransactionalWorker(TransactionRepository txRepo, OutboxRepository outboxRepo, ObjectMapper mapper) {
        this.txRepo = txRepo;
        this.outboxRepo = outboxRepo;
        this.mapper = mapper;
    }

    @Transactional
    public boolean processInternal(
            TransactionRequest request,
            String idempotencyKey) {

        // Persist transaction
        var tx = new Transaction(request, idempotencyKey);
        txRepo.save(tx);

        // Create event
        var event = TransactionReceivedEvent.from(
                request.transactionId(),
                request.customerId(),
                request.amount(),
                request.currency(),
                request.country(),
                request.deviceId(),
                request.timestamp()
        );

        var payload = mapper.writeValueAsString(event);

        // Persist outbox
        outboxRepo.save(
                new OutboxEvent(
                        request.transactionId(),
                        "TransactionReceived",
                        payload,
                        MDC.get("X-Correlation-Id")
                )
        );

        return true;
    }
}
