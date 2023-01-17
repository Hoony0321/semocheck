package com.company.semocheck.repository;

import com.company.semocheck.domain.FileDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileDetailRepository extends JpaRepository<FileDetail, String> {
}
