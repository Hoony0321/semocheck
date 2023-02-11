package com.company.semocheck.repository;

import com.company.semocheck.domain.Checklist;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.SubCategory;
import org.hibernate.annotations.Check;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {
    List<Checklist> findByPublishTrue();
    List<Checklist> findByOwner(Member owner);
}
