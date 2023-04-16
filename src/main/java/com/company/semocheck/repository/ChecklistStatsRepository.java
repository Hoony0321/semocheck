package com.company.semocheck.repository;

import com.company.semocheck.domain.checklist.ChecklistStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChecklistStatsRepository extends JpaRepository<ChecklistStats, Long> {
}
