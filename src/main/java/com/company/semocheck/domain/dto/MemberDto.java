package com.company.semocheck.domain.dto;

import com.company.semocheck.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberDto {

    private Long id;
    private String provider;
    private String email;
    private String name;
    private String picture;
    private Boolean agreeNotify;
    private Boolean sex;
    private Integer age;

    @Builder
    public MemberDto(Long id, String provider, String email, String name, String picture, Boolean agreeNotify, Boolean sex, Integer age) {
        this.id = id;
        this.provider = provider;
        this.email = email;
        this.name = name;
        this.picture = picture;
        this.agreeNotify = agreeNotify;
        this.sex = sex;
        this.age = age;
    }

    static public MemberDto createDto(Member member){
        return MemberDto.builder()
                .id(member.getId())
                .provider(member.getProvider())
                .name(member.getName())
                .email(member.getEmail())
                .picture(member.getPicture())
                .agreeNotify(member.getAgreeNotify())
                .sex(member.getSex())
                .age(member.getAge())
                .build();
    }
}
