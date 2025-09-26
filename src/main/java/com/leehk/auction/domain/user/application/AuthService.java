package com.leehk.auction.domain.user.application;

import com.leehk.auction.domain.user.dto.LoginRequestDto;
import com.leehk.auction.domain.user.dto.LoginResponseDto;
import com.leehk.auction.domain.user.dto.SignUpRequestDto;
import com.leehk.auction.global.response.CustomException;

public interface AuthService {

    /**
     * 제공된 가입 정보를 사용하여 새로운 사용자를 시스템에 등록합니다.
     *
     * @param dto 이메일, 이름, 비밀번호, 닉네임을 포함한 {@link SignUpRequestDto} 인스턴스
     */
    void signUp(SignUpRequestDto dto);

    /**
     * 제공된 인증 정보로 사용자 로그인을 처리하고
     * 액세스 토큰과 리프레시 토큰을 생성합니다.
     *
     * @param dto 사용자의 이메일과 비밀번호를 포함한 {@link LoginRequestDto} 인스턴스
     * @return 인증된 사용자의 액세스 토큰과 리프레시 토큰을 포함한 {@link LoginResponseDto} 인스턴스
     * @throws CustomException 사용자가 존재하지 않거나 비밀번호가 유효하지 않은 경우
     */
    LoginResponseDto login(LoginRequestDto dto);
}