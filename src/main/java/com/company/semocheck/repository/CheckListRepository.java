package com.company.semocheck.repository;

import com.company.semocheck.domain.CheckList;
import com.company.semocheck.domain.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CheckListRepository extends JpaRepository<CheckList, Long> {

    List<CheckList> findByCategoryIn(List<SubCategory> categories);
}
