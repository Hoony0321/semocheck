package com.company.semocheck.service;

import com.company.semocheck.auth.oauth2.OAuth2Attributes;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.dto.request.member.JoinRequestDto;
import com.company.semocheck.domain.dto.request.member.UpdateRequestDto;
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

    public List<Member> findAll(){ return memberRepository.findAll(); }
    public Member findById(Long id){
        Optional<Member> findOne = memberRepository.findById(id);
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, "해당 정보의 회원은 존재하지 않습니다.");

        return findOne.get();
    }

    public Member findByOAuthIdAndProvider(String oAuthId, String provider){
        Optional<Member> findOne = memberRepository.findByoAuthIdAndProvider(oAuthId, provider);
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, "해당 계정의 회원은 존재하지 않습니다.");

        return findOne.get();
    }

    public Optional<Member> checkByOAuthIdAndProvider(String oAuthId, String provider){
        Optional<Member> findOne = memberRepository.findByoAuthIdAndProvider(oAuthId, provider);
        return findOne;
    }

    @Transactional
    public Long join(OAuth2Attributes attributes, String provider, JoinRequestDto joinRequestDto, String fcmToken){
        Member member = Member.createEntity(attributes, provider);
        memberRepository.save(member);

        //TODO : fcmToken 세팅
        member.setInfoNewMember(joinRequestDto);

        return member.getId();
    }

    @Transactional
    public void delete(Member member){
        memberRepository.delete(member);
    }

    @Transactional
    public Member updateInfo(Member member, UpdateRequestDto requestDto) {
        member.updateInfo(requestDto);
        return member;
    }
}
