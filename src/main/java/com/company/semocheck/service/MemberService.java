package com.company.semocheck.service;

import com.company.semocheck.auth.jwt.JwtUtils;
import com.company.semocheck.auth.oauth2.OAuth2Attributes;
import com.company.semocheck.common.Constants;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.ErrorMessages;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.MemberCategory;
import com.company.semocheck.domain.SubCategory;
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
    private final MemberCategoryRepository memberCategoryRepository;
    private final CategoryService categoryService;
    private final JwtUtils jwtUtils;

    public List<Member> findAll(){ return memberRepository.findAll(); }

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
        //verify provider
        if(!provider.equals(Constants.PROVIDER_KAKAO) && !provider.equals(Constants.PROVIDER_GOOGLE)){
            throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.INVALID_PROVIDER);
        }

        Member member = Member.createEntity(attributes, provider);

        //TODO : fcmToken 세팅
        //회원 기본정보 setting
        member.setInfoNewMember(createMemberRequest);

        //category setting
        List<SubCategory> categories = new ArrayList<>();
        for (SubCategoryDto dto : createMemberRequest.getCategories()) {
            SubCategory subCategory = categoryService.findSubCategoryByName(dto.getMain(), dto.getName());
            categories.add(subCategory);
        }
        this.addMemberCategory(member, categories);

        memberRepository.save(member);

        return member.getId();
    }

    @Transactional
    public void delete(Member member){
        memberRepository.delete(member);
    }

    @Transactional
    public Member updateInfo(Member member, UpdateMemberRequest requestDto) {
        member.updateInfo(requestDto);
        return member;
    }

    public List<MemberCategoryDto> getCategories(Member member) {
        List<MemberCategoryDto> memberCategoryDtos = new ArrayList<>();
        for (MemberCategory category : member.getCategories()) {
            memberCategoryDtos.add(MemberCategoryDto.createDto(category));
        }

        return memberCategoryDtos;
    }

    @Transactional
    public void addMemberCategory(Member member, List<SubCategory> categories) {
        for (SubCategory category : categories) {
            Optional<MemberCategory> findOne = member.getCategories().stream()
                    .filter(ct -> ct.getSubCategory().getId().equals(category.getId())).findFirst();
            if(findOne.isPresent()) throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.EXISTED_CATEGORY);

            MemberCategory memberCategory = MemberCategory.createEntity(member, category);
            member.addCategory(memberCategory);
        }
    }

    @Transactional
    public void updateMemberCategory(Member member, List<SubCategory> categories) {
        // clear memberCategory
        for (MemberCategory category : member.getCategories()) {
            memberCategoryRepository.delete(category);
        }

        // add new memberCategory
        List<MemberCategory> memberCategories = new ArrayList<>();
        for(SubCategory category : categories){
            MemberCategory memberCategory = MemberCategory.createEntity(member, category);
            memberCategoryRepository.save(memberCategory);
            memberCategories.add(memberCategory);
        }

        member.setCategory(memberCategories);
    }

    @Transactional
    public void deleteMemberCategory(Member member, List<SubCategory> categories) {
        for(SubCategory category : categories){
            Optional<MemberCategory> findOne = member.getCategories().stream()
                    .filter(ct -> ct.getSubCategory().getId().equals(category.getId())).findFirst();
            if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, ErrorMessages.NOT_FOUND_CHECKLIST);

            member.removeCategory(findOne.get());
            memberCategoryRepository.delete(findOne.get());
        }
    }


}
