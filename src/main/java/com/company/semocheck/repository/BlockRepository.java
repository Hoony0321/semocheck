package com.company.semocheck.repository;


import com.company.semocheck.domain.checklist.Block;
import com.company.semocheck.domain.checklist.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {

    List<Block> findByChecklist(Checklist checklist);
}
