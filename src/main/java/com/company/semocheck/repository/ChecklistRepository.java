package com.company.semocheck.repository;

import com.company.semocheck.domain.Checklist;
import com.company.semocheck.domain.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {

    List<Checklist> findByCategoryIn(List<SubCategory> categories);
}
