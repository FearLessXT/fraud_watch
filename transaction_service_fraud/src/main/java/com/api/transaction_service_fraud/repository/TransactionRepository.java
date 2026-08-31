package com.api.transaction_service_fraud.repository;

import com.api.transaction_service_fraud.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    boolean existsByIdempotencyKey(String idempotencyKey);
}
