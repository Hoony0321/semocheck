package com.company.semocheck.repository;

import com.company.semocheck.domain.Report;
import com.company.semocheck.domain.checklist.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByChecklist(Checklist checklist);
}
