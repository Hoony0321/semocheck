package com.company.semocheck.repository;


import com.company.semocheck.domain.checklist.BlockedChecklist;
import com.company.semocheck.domain.checklist.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockedChecklistRepository extends JpaRepository<BlockedChecklist, Long> {

    List<BlockedChecklist> findByChecklist(Checklist checklist);
}
