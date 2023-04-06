package com.company.semocheck.service;

import com.company.semocheck.domain.Inquiry;
import com.company.semocheck.domain.InquiryComment;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.request.inquiry.CreateInquiryCommentRequest;
import com.company.semocheck.repository.InquiryCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryCommentService {

    private final InquiryCommentRepository inquiryCommentRepository;

    @Transactional
    public void createInquiryComment(Inquiry inquiry, Member writer, CreateInquiryCommentRequest createRequest) {
        InquiryComment comment = InquiryComment.createEntity(createRequest);

        //연관관계 설정
        comment.setInquiry(inquiry);
        comment.setWriter(writer);
        inquiry.addInquiryComment(comment);



        inquiryCommentRepository.save(comment);
    }

    @Transactional
    public void deleteInquiryComment(Long id) {
        inquiryCommentRepository.deleteById(id);
    }
}
