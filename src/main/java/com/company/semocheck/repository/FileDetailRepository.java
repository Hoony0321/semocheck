package com.company.semocheck.repository;

import com.company.semocheck.domain.FileDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileDetailRepository extends JpaRepository<FileDetail, String> {

    public List<FileDetail> findAllByFolder(String folder);
}
