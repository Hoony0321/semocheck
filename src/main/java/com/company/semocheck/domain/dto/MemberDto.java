package com.company.semocheck.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

@Getter
@NoArgsConstructor
public class MemberDto {
    private String email;
    private String name;
    private String picture;

    @Builder
    public MemberDto(String email, String name, String picture) {
        this.email = email;
        this.name = name;
        this.picture = picture;
    }

    static public MemberDto createDto(OAuth2User oAuth2User){
        Map<String, Object> attributes = oAuth2User.getAttributes();
        return MemberDto.builder()
                .email((String) attributes.get("email"))
                .name((String) attributes.get("name"))
                .picture((String) attributes.get("picture"))
                .build();

    }
}
