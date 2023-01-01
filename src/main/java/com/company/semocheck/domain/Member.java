package com.company.semocheck.domain;

import com.company.semocheck.auth.oauth2.OAuth2Attributes;
import com.company.semocheck.domain.dto.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@NoArgsConstructor
@DynamicInsert
public class Member extends BaseTimeEntity{

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(name = "oauth_id", nullable = false)
    private String oAuthId;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String email;

    @Size(max = 15)
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private Boolean agree_notify;

    private Boolean sex;

    private Integer age;

    @Builder
    public Member(String oAuthId, String provider, String email, String name, String picture, Role role, Boolean agree_notify, Boolean sex, Integer age) {
        this.oAuthId = oAuthId;
        this.provider = provider;
        this.email = email;
        this.name = name;
        this.picture = picture;
        this.role = role;
        this.agree_notify = agree_notify;
        this.sex = sex;
        this.age = age;
    }

    public String getRoleKey(){
        return this.role.getKey();
    }

    static public Member createNewMember(OAuth2Attributes attributes, String provider){
        Member member = new Member();
        member.oAuthId = attributes.getId();
        member.provider = provider;
        member.email = attributes.getEmail();
        member.picture = attributes.getPicture();
        member.name = attributes.getName();
        member.role = Role.USER;
        member.agree_notify = false;

        return member;
    }
}
