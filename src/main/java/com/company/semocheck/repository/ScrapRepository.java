package com.company.semocheck.repository;

import com.company.semocheck.domain.Checklist;
import com.company.semocheck.domain.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    Optional<Scrap> findByChecklist(Checklist checklist);
}
