package com.company.semocheck.repository;

import com.company.semocheck.domain.inquiry.InquiryComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryCommentRepository extends JpaRepository<InquiryComment, Long> {
}
