package com.leehk.auction.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor  // test 용
public class SignUpRequestDto {

    private String email;
    private String name;
    private String password;
    private String nickname;
}
