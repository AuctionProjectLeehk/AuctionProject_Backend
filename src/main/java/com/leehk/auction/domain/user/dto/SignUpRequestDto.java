package com.leehk.auction.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequestDto {

    private String email;
    private String name;
    private String password;
    private String nickname;
}
