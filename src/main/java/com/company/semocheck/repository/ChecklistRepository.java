package com.company.semocheck.repository;

import com.company.semocheck.domain.checklist.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {

    Optional<Checklist> findByIdAndTemporaryIsNull(Long id);
    Optional<Checklist> findByIdAndTemporaryIsNotNull(Long id);
    List<Checklist> findByTemporaryIsNull();
    List<Checklist> findByTemporaryIsNullAndPublishIsTrue();

//    List<Checklist> findTop10AndTemporaryIsNullAndPublishIsTrueByOrderByViewCountDesc();

}
