package com.company.semocheck.service;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.domain.Inquiry;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.request.inquiry.CreateInquiryRequest;
import com.company.semocheck.domain.request.inquiry.UpdateInquiryRequest;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {
    private final InquiryRepository inquiryRepository;


    public Inquiry findById(Long id){
        Optional<Inquiry> findOne = inquiryRepository.findById(id);
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND);

        return findOne.get();
    }

    public List<Inquiry> findByMember(Member member){
        return member.getInquiries();
    }

    @Transactional
    public Long createInquiry(Member member, CreateInquiryRequest request){
        Inquiry inquiry = Inquiry.createEntity(member, request);

        //연관관계 설정
        inquiry.setMember(member);
        member.addInquiry(inquiry);

        inquiryRepository.save(inquiry);

        return inquiry.getId();
    }

    @Transactional
    public void updateInquiry(Inquiry inquiry, UpdateInquiryRequest updateRequest) {
        inquiry.updateInfo(updateRequest);
    }

    @Transactional
    public void deleteInquiry(Inquiry inquiry) {
        //연관관계 제거
        inquiry.getMember().removeInquiry(inquiry);
        inquiry.setMember(null); // comments는 cascade로 제거

        inquiryRepository.delete(inquiry);
    }
}
