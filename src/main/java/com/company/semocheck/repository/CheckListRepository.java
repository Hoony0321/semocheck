package com.company.semocheck.repository;

import com.company.semocheck.domain.CheckList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckListRepository extends JpaRepository<CheckList, Long> {
}
