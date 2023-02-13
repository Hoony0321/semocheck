package com.company.semocheck.repository;

import com.company.semocheck.domain.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    public List<SubCategory> findByName(String name);
}
