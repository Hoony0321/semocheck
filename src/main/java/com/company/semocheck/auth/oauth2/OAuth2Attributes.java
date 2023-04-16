package com.company.semocheck.auth.oauth2;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.ErrorMessages;
import com.company.semocheck.exception.GeneralException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Getter
public class OAuth2Attributes {

    private String id;
    private String name;
    private String email;
    private String picture;

    public OAuth2Attributes(String id, String name, String email, String picture) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    public static Map<String, Object> getOAuthInfo(String oAuthToken, String provider){
        RestTemplate restTemplate = new RestTemplate();
        URI requestUrl;
        ResponseEntity<Object> responseEntity;
        RequestEntity requestEntity = null;

        // TODO : apple sns login 추가하기
        switch (provider) {
            case "kakao" :
                requestUrl = UriComponentsBuilder.newInstance()
                        .scheme("https")
                        .host("kapi.kakao.com")
                        .path("/v2/user/me")
                        .encode().build().toUri();

                //OAuth token을 통해 리소스 서버로 사용자 정보 요청
                requestEntity = RequestEntity
                        .post(requestUrl)
                        .header("Authorization", "Bearer " + oAuthToken)
                        .body(null);
                break;
            case "google" :
                requestUrl = UriComponentsBuilder.newInstance()
                        .scheme("https")
                        .host("www.googleapis.com")
                        .path("/userinfo/v2/me")
                        .queryParam("access_token", oAuthToken)
                        .encode().build().toUri();
                break;
            default :
                throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.JWT_INVALID_PROVIDER);
        }

        try{
            if(requestEntity == null) responseEntity = restTemplate.getForEntity(requestUrl, Object.class);
            else responseEntity = restTemplate.postForEntity(requestUrl, requestEntity, Object.class);}
        catch (Exception e){
            throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.FAIL_AUTHENTICATION_OAUTH);}

        //받아온 사용자 정보를 동일한 form으로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> oAuth2Info = objectMapper.convertValue(responseEntity.getBody(), Map.class);

        return oAuth2Info;
    }

    public static OAuth2Attributes of(String registrationId, Map<String, Object> attributes){
        return switch (registrationId) {
            case "google" -> ofGoogle(attributes);
            case "kakao" -> ofKakao(attributes);
            default -> throw new GeneralException(Code.BAD_REQUEST);
        };
    }

//    private static OAuth2Attributes ofNaver(Map<String, Object> attributes) {
//        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
//
//        return new OAuth2Attributes((String)response.get("name"), (String)response.get("email"),
//                (String) response.get("profile_image"));
//
//    }

    private static OAuth2Attributes ofKakao(Map<String, Object> attributes) {
        // kakao는 kakao_account에 유저정보가 있다. (email)
        Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
        // kakao_account안에 또 profile이라는 JSON객체가 있다. (nickname, profile_image)
        Map<String, Object> kakaoProfile = (Map<String, Object>)kakaoAccount.get("profile");

        String oAuthId = Long.toString((Long) attributes.get("id"));
        String name = (String) kakaoProfile.get("nickname");
        String email = (String) kakaoAccount.get("email");
        String profile = (String) kakaoProfile.get("profile_image_url");

        if(oAuthId == null || name == null || email == null)
            throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.INVAILD_ARGUMENT);

        return new OAuth2Attributes(oAuthId, name, email, profile);
    }

    private static OAuth2Attributes ofGoogle(Map<String, Object> attributes) {

        String oAuthId = (String) attributes.get("id");
        String name = (String) attributes.get("name");
        String email = (String) attributes.get("email");
        String profile = (String) attributes.get("picture");

        if(oAuthId == null || name == null || email == null)
            throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.INVAILD_ARGUMENT);

        return new OAuth2Attributes(oAuthId, name, email, profile);
    }

}
