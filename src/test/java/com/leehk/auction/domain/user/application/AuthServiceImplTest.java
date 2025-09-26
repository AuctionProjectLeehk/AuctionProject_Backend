package com.leehk.auction.domain.user.application;

import com.leehk.auction.domain.auction.BaseH2Test;
import com.leehk.auction.domain.user.dto.LoginRequestDto;
import com.leehk.auction.domain.user.dto.LoginResponseDto;
import com.leehk.auction.domain.user.dto.SignUpRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuthServiceImplTest extends BaseH2Test {

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("회원가입 후 로그인 성공 테스트")
    void signUpAndLoginTest_Success() {
        // given
        String userEmail = "email@act.ion";
        String password = "<PASSWORD>";
        String nickname = "test";
        String name = "test";

        SignUpRequestDto signUpDto = new SignUpRequestDto(userEmail, name, password, nickname);
        LoginRequestDto loginDto = new LoginRequestDto(userEmail, password);

        // when
        authService.signUp(signUpDto);
        LoginResponseDto response = authService.login(loginDto);

        // then
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
    }
}