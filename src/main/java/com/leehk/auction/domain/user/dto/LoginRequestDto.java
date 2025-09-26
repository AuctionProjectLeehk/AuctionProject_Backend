package com.leehk.auction.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor  // test 용
public class LoginRequestDto {

    private String email;
    private String password;
}
