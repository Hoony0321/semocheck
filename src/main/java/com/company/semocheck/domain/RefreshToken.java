package com.company.semocheck.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "refresh_toekn")
public class RefreshToken {

    @Id
    @Column(name = "rt_key")
    private Long key;

    @NotNull
    @Column(name = "rt_value")
    private String value;

    @Builder
    public RefreshToken(Long key, String value) {
        this.key = key;
        this.value = value;
    }

    public void updateValue(String token){
        this.value = token;
    }
}
