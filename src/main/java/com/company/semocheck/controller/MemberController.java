package com.company.semocheck.controller;

import com.company.semocheck.auth.jwt.JwtUtils;
import com.company.semocheck.auth.oauth2.OAuth2Attributes;
import com.company.semocheck.common.response.ApiDocumentResponse;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.common.response.ResponseDto;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.dto.MemberDto;
import com.company.semocheck.domain.dto.request.member.JoinRequestDto;
import com.company.semocheck.domain.dto.request.member.UpdateRequestDto;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.service.MemberService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Tag(name = "회원", description = "회원 관련 API 모음입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final JwtUtils jwtUtils;
    private final MemberService memberService;



    @ApiDocumentResponse
    @Operation(summary = "Sign up API", description = "oAuthToken 및 회원정보를 입력받아 해당 계정을 회원으로 등록합니다.\n\n" +
            "return data : 회원가입 성공한 member id\n\n")
    @PostMapping("")
    public DataResponseDto<Long> joinMember(@RequestParam("oAuthToken") String oAuthToken, @RequestParam("provider") String provider,
                                            @RequestParam("fcmToken") String fcmToken, @RequestBody JoinRequestDto joinRequestDto){

        Map<String, Object> oAuth2Info = OAuth2Attributes.getOAuthInfo(oAuthToken, provider);
        OAuth2Attributes attributes = OAuth2Attributes.of(provider, oAuth2Info);

        Optional<Member> findOne = memberService.checkByOAuthIdAndProvider(attributes.getId(), provider);
        if(findOne.isPresent()) throw new GeneralException(Code.BAD_REQUEST, "이미 존재하는 회원입니다.");

        //새로운 회원 생성 & 초기 정보 세팅
        Long id = memberService.join(attributes, provider, joinRequestDto, fcmToken);

        return DataResponseDto.of(id, "회원가입 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Get member's detail info API", description = "member id를 통해 단일 회원 정보를 조회합니다.\n\n" +
            "해당 회원의 JWT 토큰으로만 접근 가능한 URL입니다.")
    @GetMapping("/{id}")
    public DataResponseDto<MemberDto> getMember(@PathVariable("id") Long id, HttpServletRequest request){

        String accessToken = jwtUtils.getAccessToken(request);
        Claims claims = jwtUtils.parseClaims(accessToken);
        Member findOne = memberService.findByOAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));

        if(!findOne.getId().equals(id)) throw new GeneralException(Code.FORBIDDEN);

        return DataResponseDto.of(MemberDto.createDto(findOne), "회원 정보 조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Delte member's info in database API", description = "member id를 통해 해당 회원을 DB에서 삭제합니다.\n\n" +
            "해당 회원의 JWT 토큰으로만 접근 가능한 URL입니다.")
    @DeleteMapping("/{id}")
    public ResponseDto deleteMember(@PathVariable("id") Long id, HttpServletRequest request){
        //JWT Member 검증
        String accessToken = jwtUtils.getAccessToken(request);
        Claims claims = jwtUtils.parseClaims(accessToken);
        Member member = memberService.findByOAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));
        if(!member.getId().equals(id)) throw new GeneralException(Code.FORBIDDEN);

        memberService.delete(member);

        return ResponseDto.of(true, "회원 삭제 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Update member's info API", description = "member id를 통해 해당 회원 정보를 수정합니다.\n\n" +
            "모든 정보 수정이 가능한 API입니다.\n" +
            "만약 수정을 원치 않는 정보는 null로 넣어주시면 됩니다.\n" +
            "해당 회원의 JWT 토큰으로만 접근 가능한 URL입니다.")
    @PutMapping("/{id}")
    public DataResponseDto<MemberDto> updateMember(@PathVariable("id") Long id, HttpServletRequest request, @RequestBody UpdateRequestDto requestDto) {
        //JWT Member 검증
        String accessToken = jwtUtils.getAccessToken(request);
        Claims claims = jwtUtils.parseClaims(accessToken);
        Member member = memberService.findByOAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));
        if(!member.getId().equals(id)) throw new GeneralException(Code.FORBIDDEN);

        Member updatedMember = memberService.updateInfo(member, requestDto);
        return DataResponseDto.of(MemberDto.createDto(updatedMember), "회원 정보 수정 성공");
    }

}
