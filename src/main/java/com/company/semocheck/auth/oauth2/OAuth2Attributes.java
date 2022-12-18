package com.company.semocheck.auth.oauth2;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.exception.GeneralException;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuth2Attributes {
    private String name;
    private String email;
    private String picture;

    public OAuth2Attributes(String name, String email, String picture) {
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    public static OAuth2Attributes of(String registrationId, Map<String, Object> attributes){
        return switch (registrationId) {
            case "google" -> ofGoogle(attributes);
            case "kakao" -> ofKakao(attributes);
            case "naver" -> ofNaver(attributes);
            default -> throw new GeneralException(Code.BAD_REQUEST);
        };


    }

    private static OAuth2Attributes ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return new OAuth2Attributes((String)response.get("name"), (String)response.get("email"),
                (String) response.get("profile_image"));

    }

    private static OAuth2Attributes ofKakao(Map<String, Object> attributes) {
        // kakao는 kakao_account에 유저정보가 있다. (email)
        Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
        // kakao_account안에 또 profile이라는 JSON객체가 있다. (nickname, profile_image)
        Map<String, Object> kakaoProfile = (Map<String, Object>)kakaoAccount.get("profile");

        return new OAuth2Attributes((String) kakaoProfile.get("nickname"),(String) kakaoAccount.get("email"),
                (String) kakaoProfile.get("profile_image_url"));
    }

    private static OAuth2Attributes ofGoogle(Map<String, Object> attributes) {
        return new OAuth2Attributes((String) attributes.get("name"), (String) attributes.get("email"),
                (String) attributes.get("picture"));
    }

}
