package com.company.semocheck.repository;

import com.company.semocheck.domain.checklist.Checklist;
import com.company.semocheck.domain.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    List<Scrap> findByChecklist(Checklist checklist);

}
