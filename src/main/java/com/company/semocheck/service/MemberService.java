package com.company.semocheck.service;

import com.company.semocheck.auth.jwt.JwtUtils;
import com.company.semocheck.auth.oauth2.OAuth2Attributes;
import com.company.semocheck.common.Constants;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.ErrorMessages;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.domain.member.MemberCategory;
import com.company.semocheck.domain.category.SubCategory;
import com.company.semocheck.domain.dto.category.MemberCategoryDto;
import com.company.semocheck.domain.dto.category.SubCategoryDto;
import com.company.semocheck.domain.request.member.CreateMemberRequest;
import com.company.semocheck.domain.request.member.UpdateMemberRequest;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.repository.MemberCategoryRepository;
import com.company.semocheck.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final CategoryService categoryService;
    private final JwtUtils jwtUtils;

    public Member getMemberByJwt(HttpServletRequest request){
        String accessToken = jwtUtils.getAccessToken(request);
        Claims claims = jwtUtils.parseClaims(accessToken);
        Member member = findByOAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));
        return member;
    }

    public Optional<Member> getMemberByJwtNoError(HttpServletRequest request){
        String accessToken = jwtUtils.getAccessToken(request);
        if(accessToken != null){
            Claims claims = jwtUtils.parseClaims(accessToken);
            Optional<Member> findOne = memberRepository.findByoAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));
            return findOne;
        }
        else{
            return Optional.empty();
        }
    }
    public Member findById(Long id){
        Optional<Member> findOne = memberRepository.findById(id);
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, ErrorMessages.NOT_FOUND_MEMBER);

        return findOne.get();
    }

    public Member findByOAuthIdAndProvider(String oAuthId, String provider){
        Optional<Member> findOne = memberRepository.findByoAuthIdAndProvider(oAuthId, provider);
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, ErrorMessages.NOT_FOUND_MEMBER);

        return findOne.get();
    }

    public Optional<Member> checkByOAuthIdAndProvider(String oAuthId, String provider){
        Optional<Member> findOne = memberRepository.findByoAuthIdAndProvider(oAuthId, provider);
        return findOne;
    }

    @Transactional
    public Long createMember(OAuth2Attributes attributes, String provider, CreateMemberRequest createMemberRequest, String fcmToken){
        //중복 회원 체크
        Optional<Member> findOne = checkByOAuthIdAndProvider(attributes.getId(), provider);
        if(findOne.isPresent()) throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.EXISTED_MEMBER);

        //verify provider
        if(!provider.equals(Constants.PROVIDER_KAKAO) && !provider.equals(Constants.PROVIDER_GOOGLE)){
            throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.INVALID_PROVIDER);
        }

        Member member = Member.createEntity(attributes, provider);

        //TODO : fcmToken 세팅
        //회원 기본정보 setting
        member.setInfoNewMember(createMemberRequest);

        memberRepository.save(member);

        return member.getId();
    }

    @Transactional
    public Member updateInfo(Member member, UpdateMemberRequest requestDto) {
        member.updateInfo(requestDto);
        return member;
    }

    @Transactional
    public void delete(Member member){
        memberRepository.delete(member);
    }

}
