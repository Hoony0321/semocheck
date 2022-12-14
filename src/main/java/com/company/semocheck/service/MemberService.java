package com.company.semocheck.service;

import com.company.semocheck.domain.Member;
import com.company.semocheck.exception.ApiException;
import com.company.semocheck.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public List<Member> findAll(){ return memberRepository.findAll(); }

    public Member findByEmail(String email){
        Optional<Member> findOne = memberRepository.findByEmail(email);
        if(findOne.isEmpty()) throw new ApiException(HttpStatus.NOT_FOUND, "해당 이메일 회원은 존재하지 않습니다.");

        return findOne.get();
    }
}
