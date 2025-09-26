package com.leehk.auction.domain.user.presentation;

import com.leehk.auction.domain.user.application.AuthService;
import com.leehk.auction.domain.user.application.UserService;
import com.leehk.auction.domain.user.domain.User;
import com.leehk.auction.domain.user.dto.LoginRequestDto;
import com.leehk.auction.domain.user.dto.LoginResponseDto;
import com.leehk.auction.domain.user.dto.SignUpRequestDto;
import com.leehk.auction.global.auth.JwtTokenProvider;
import com.leehk.auction.global.response.ApiResponse;
import com.leehk.auction.global.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ApiResponse<String> signUp(
            @RequestBody SignUpRequestDto signUpRequestDto) {
        authService.signUp(signUpRequestDto);
        
        return ApiResponse.success(SuccessCode.CREATED, "회원가입 완료");
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(
            @RequestBody LoginRequestDto loginRequestDto) {

        return ApiResponse.success(SuccessCode.OK, authService.login(loginRequestDto));
    }
}
