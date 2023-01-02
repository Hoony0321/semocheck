package com.company.semocheck.domain.dto.response;

import com.company.semocheck.domain.dto.Token;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponseDto {
    Long id;
    String accessToken;
    String refreshToken;

    @Builder
    public LoginResponseDto(Long id, String accessToken, String refreshToken) {
        this.id = id;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
