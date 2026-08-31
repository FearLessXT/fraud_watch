package com.api.transaction_service_fraud.service;

import com.api.transaction_service_fraud.dto.TransactionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionalWorker worker;

    public TransactionService(TransactionalWorker worker) {
        this.worker = worker;
    }

    public boolean process(
            TransactionRequest request,
            String idempotencyKey) {

        try {
            return worker.processInternal(request, idempotencyKey);
        } catch (DataIntegrityViolationException ex) {

            log.debug(
                    "Duplicate transaction detected: transactionId={}, idempotencyKey={}",
                    request.transactionId(),
                    idempotencyKey
            );

            return false;
        }
    }
}
