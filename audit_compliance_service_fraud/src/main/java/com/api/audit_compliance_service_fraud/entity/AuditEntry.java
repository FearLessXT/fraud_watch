package com.api.audit_compliance_service_fraud.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Entity
@Table(name="audit_entry")
@Getter
@AllArgsConstructor
public class AuditEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private String decision;

    private String action;

    @Column(nullable = false)
    private int riskScore;

    @Column(nullable = false)
    private String riskLevel;

    @Column(columnDefinition = "json")
    private String reasons;

    private String correlationId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private int eventVersion;

    @Column(nullable = false, updatable = false)
    private Instant recordedAt = Instant.now();

    protected AuditEntry() {}

    public AuditEntry(
            String transactionId,
            String decision,
            String action,
            int riskScore,
            String riskLevel,
            String reasons,
            String correlationId,
            String eventType,
            int eventVersion) {

        this.transactionId = transactionId;
        this.decision = decision;
        this.action = action;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.reasons = reasons;
        this.correlationId = correlationId;
        this.eventType = eventType;
        this.eventVersion = eventVersion;
    }
}
