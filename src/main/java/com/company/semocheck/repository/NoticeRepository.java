package com.company.semocheck.repository;

import com.company.semocheck.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
