package com.company.semocheck.service;

import com.company.semocheck.auth.oauth2.OAuth2Attributes;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.domain.Member;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public Long joinMember(OAuth2Attributes attributes, String provider){
        Member member = Member.createNewMember(attributes, provider);
        memberRepository.save(member);

        return member.getId();
    }

    public List<Member> findAll(){ return memberRepository.findAll(); }
    public Member findById(Long id){
        Optional<Member> findOne = memberRepository.findById(id);
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, "해당 정보의 회원은 존재하지 않습니다.");

        return findOne.get();
    }

    public Member findByEmail(String email){
        Optional<Member> findOne = memberRepository.findByEmail(email);
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, "해당 정보의 회원은 존재하지 않습니다.");

        return findOne.get();
    }

    public Optional<Member>  findByOAuthIdAndProvider(String oAuthId, String provider){
        return memberRepository.findByoAuthIdAndProvider(oAuthId, provider);

    }
}
