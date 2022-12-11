package com.company.semocheck.oauth;

import com.company.semocheck.domain.User;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    private String name;
    private String emial;
    private String picture;

    public SessionUser(User user){
        this.name = user.getName();
        this.emial = user.getEmail();
        this.picture = user.getPicture();
    }
}
