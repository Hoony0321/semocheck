package com.company.semocheck.common.response;

import lombok.Getter;

@Getter
public enum ErrorMessages {

    //members
    NOT_FOUND_MEMBER("해당 회원을 찾을 수 없습니다."),
    INVALID_PROVIDER("유효하지 않은 provider입니다."),
    EXISTED_MEMBER("해당 회원은 이미 존재합니다."),

    //jwt
    JWT_INVALID_PROVIDER("유효하지 않은 provider입니다."),
    FAIL_AUTHENTICATION_OAUTH("OAuth 인증에 실패했습니다"),


    //reports
    INVALID_REPORT_TYPE("잘못된 신고 유형입니다."),

    //scraps
    EXISTED_SCRAP("이미 스크랩한 체크리스트입니다."),
    NOT_FOUND_SCRAP("해당 스크랩을 찾을 수 없습니다."),

    //categories
    EXISTED_CATEGORY("해당 카테고리는 이미 존재합니다."),
    EXISTED_SUB_CATEGORY("연결된 하위 카테고리가 존재합니다."),
    NOT_EXISTED_MAIN_CATEGORY("해당 1차 카테고리를 찾을 수 없습니다."),
    NOT_EXISTED_SUB_CATEGORY("해당 2차 카테고리를 찾을 수 없습니다."),

    NOT_FOUND_CATEGORY("해당 카테고리를 찾을 수 없습니다."),

    //checklists
    NOT_FOUND_CHECKLIST("해당 체크리스트를 찾을 수 없습니다."),
    NOT_FOUND_IMAGE("해당 이미지를 찾을 수 없습니다."),
    NOT_PUBLISHED("해당 체크리스트는 공개할 수 없습니다."),
    INVALID_STEP("스텝 정보가 유효하지 않습니다."),

    //files
    NOT_FOUND_FILE("파일을 찾을 수 없습니다"),
    NOT_FOUND_OBJECT("object를 찾을 수 없습니다."),
    INVALID_OBJECT("잘못된 object 형식입니다."),
    EXISTED_FILE("이미 존재하는 파일입니다."),

    //scraps
    CANT_OWNED_SCRAP("자신의 체크리스트를 스크랩할 수 없습니다."),
    CANT_PRIAVTE_SCRAP("비공개 체크리스트는 스크랩할 수 없습니다."),

    //general
    INVAILD_ARGUMENT("유효하지 않은 인자입니다.");

    private final String message;
    ErrorMessages(String message){
        this.message = message;
    }
}
