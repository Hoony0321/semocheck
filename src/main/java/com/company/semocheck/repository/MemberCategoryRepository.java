package com.company.semocheck.repository;

import com.company.semocheck.domain.MemberCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberCategoryRepository extends JpaRepository<MemberCategory, Long> {
}
