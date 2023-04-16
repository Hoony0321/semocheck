package com.company.semocheck.repository;

import com.company.semocheck.domain.member.MemberCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberCategoryRepository extends JpaRepository<MemberCategory, Long> {
}
