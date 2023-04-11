package com.company.semocheck.repository;

import com.company.semocheck.domain.ChecklistUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChecklistUsageRepository extends JpaRepository<ChecklistUsage, Long> {
}
