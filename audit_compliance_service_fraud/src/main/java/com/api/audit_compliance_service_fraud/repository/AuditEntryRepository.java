package com.api.audit_compliance_service_fraud.repository;

import com.api.audit_compliance_service_fraud.entity.AuditEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditEntryRepository extends JpaRepository<AuditEntry, Long> {

}
