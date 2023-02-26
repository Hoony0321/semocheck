package com.company.semocheck.service;

import com.company.semocheck.auth.jwt.JwtUtils;
import com.company.semocheck.auth.oauth2.OAuth2Attributes;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.MemberCategory;
import com.company.semocheck.domain.SubCategory;
import com.company.semocheck.domain.dto.category.MemberCategoryDto;
import com.company.semocheck.domain.dto.category.SubCategoryDto;
import com.company.semocheck.domain.request.member.JoinRequestDto;
import com.company.semocheck.domain.request.member.UpdateRequestDto;
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

        //TODO : fcmToken 세팅
        //회원 기본정보 setting
        member.setInfoNewMember(joinRequestDto);

        //category setting
        for (SubCategoryDto dto : joinRequestDto.getCategories()) {
            SubCategory subCategory = categoryService.findSubCategoryByName(dto.getMain(), dto.getName());
            this.addMemberCategory(member, subCategory);
        }

        memberRepository.save(member);

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

    public List<MemberCategoryDto> getCategories(Member member) {
        List<MemberCategoryDto> memberCategoryDtos = new ArrayList<>();
        for (MemberCategory category : member.getCategories()) {
            memberCategoryDtos.add(MemberCategoryDto.createDto(category));
        }

        return memberCategoryDtos;
    }

    @Transactional
    public void addMemberCategory(Member member, SubCategory category) {
        Optional<MemberCategory> findOne = member.getCategories().stream()
                .filter(ct -> ct.getSubCategory().getId().equals(category.getId())).findFirst();
        if(findOne.isPresent()) throw new GeneralException(Code.BAD_REQUEST, "이미 동일한 카테고리가 존재합니다.");

        MemberCategory memberCategory = MemberCategory.createEntity(member, category);
        member.addCategory(memberCategory);
    }

    @Transactional
    public void deleteMemberCategory(Member member, SubCategory category) {
        Optional<MemberCategory> findOne = member.getCategories().stream()
                .filter(ct -> ct.getSubCategory().getId().equals(category.getId())).findFirst();
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, "관심 카테고리 리스트에 해당 이름의 카테고리는 존재하지 않습니다.");

        member.removeCategory(findOne.get());
        memberCategoryRepository.delete(findOne.get());
    }
}
