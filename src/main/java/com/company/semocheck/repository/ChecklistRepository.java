package com.company.semocheck.repository;

import com.company.semocheck.domain.Checklist;
import com.company.semocheck.domain.Member;
import org.hibernate.annotations.Check;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {

    Optional<Checklist> findByIdAndTemporaryIsNull(Long id);
    Optional<Checklist> findByIdAndTemporaryIsNotNull(Long id);
    List<Checklist> findByTemporaryIsNullAndPublishIsTrue();
    List<Checklist> findByOwnerAndTemporaryIsNull(Member owner);
    List<Checklist> findByOwnerAndTemporaryIsNotNull(Member owner);
    List<Checklist> findTop10ByOrderByViewCountDesc();

//    List<Checklist> findTop10AndTemporaryIsNullAndPublishIsTrueByOrderByViewCountDesc();

}
