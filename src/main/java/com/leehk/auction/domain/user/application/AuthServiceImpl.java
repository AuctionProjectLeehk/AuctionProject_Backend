package com.leehk.auction.domain.user.application;

import com.leehk.auction.domain.user.domain.User;
import com.leehk.auction.domain.user.dto.LoginRequestDto;
import com.leehk.auction.domain.user.dto.LoginResponseDto;
import com.leehk.auction.domain.user.dto.SignUpRequestDto;
import com.leehk.auction.global.auth.JwtTokenProvider;
import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void signUp(SignUpRequestDto dto) {
        if (userService.isUserExistByEmail(dto.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_USER);
        }

        User user = User.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .password(dto.getPassword())  //TODO: 암호화 할것
                .nickname(dto.getNickname())
                .publicId(UUID.randomUUID())
                .build();

        userService.saveUser(user);
    }

    @Override
    public LoginResponseDto login(LoginRequestDto dto) {
        User user = userService.getUserByEmail(dto.getEmail());

        if (!user.getPassword().equals(dto.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user);
        return new LoginResponseDto(accessToken, refreshToken);
    }
}
