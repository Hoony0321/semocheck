package com.company.semocheck.repository;

import com.company.semocheck.domain.MainCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MainCategoryRepository extends JpaRepository<MainCategory, Long> {

    public Optional<MainCategory> findByName(String name);
}
