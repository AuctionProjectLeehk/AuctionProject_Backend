package com.leehk.auction.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 Bad Request
    INVALID_PARAMETER(40001, HttpStatus.BAD_REQUEST, "파라미터가 올바르지 않습니다."),
    BID_TOO_LOW(40002, HttpStatus.BAD_REQUEST, "입찰 금액이 현재 최고가보다 낮습니다."),
    AUCTION_ALREADY_ENDED(40003, HttpStatus.BAD_REQUEST, "이미 종료된 경매입니다."),
    INVALID_PASSWORD(40004, HttpStatus.BAD_REQUEST, "잘못된 비밀번호 입니다."),
    INVALID_AUTO_BID_CURRENT_PRICE(40005, HttpStatus.BAD_REQUEST, "현재 입찰가는 최대 입찰가 이하이어야 합니다."),
    INVALID_AUTO_BID_MAX_PRICE(40006, HttpStatus.BAD_REQUEST, "최대 입찰가는 현재 입찰가 이상이어야 합니다."),
    INVALID_AUTO_BID_CREATE(40007, HttpStatus.BAD_REQUEST, "자동 입찰 생성이 불가능합니다."),
    BAD_REQUEST(40100, HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    // 401 Unauthorized
    TOKEN_EXPIRED(40101, HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_TOKEN(40102, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),
    UNAUTHORIZED_BID_ACTION(40103, HttpStatus.UNAUTHORIZED, "유효하지 않은 입찰에 접근하였습니다."),
    UNAUTHORIZED_AUCTION_ACTION(40104, HttpStatus.UNAUTHORIZED, "유효하지 않은 경매에 접근하였습니다."),
    OWNER_CANNOT_BID(40105, HttpStatus.UNAUTHORIZED, "경매의 소유자는 입찰할 수 없습니다."),
    OWNER_CANNOT_AUTO_BID(40106, HttpStatus.UNAUTHORIZED, "경매의 소유자는 자동 입찰할 수 없습니다."),
    UNAUTHORIZED(40100, HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),

    // 403 Forbidden
    FORBIDDEN(40300, HttpStatus.FORBIDDEN, "권한이 없습니다."),

    // 404 Not Found
    USER_NOT_FOUND(40401, HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    AUCTION_NOT_FOUND(40402, HttpStatus.NOT_FOUND, "경매를 찾을 수 없습니다."),
    BID_NOT_FOUND(40403, HttpStatus.NOT_FOUND, "입찰을 찾을 수 없습니다."),
    AUTO_BID_NOT_FOUND(40404, HttpStatus.NOT_FOUND, "자동 입찰을 찾을 수 없습니다."),
    NOT_FOUND(40400, HttpStatus.NOT_FOUND, "찾을 수 없습니다."),

    // 409 Conflict
    DUPLICATE_USER(40901, HttpStatus.CONFLICT, "이미 존재하는 유저입니다."),

    // 500 Internal Server Error
    TOKEN_PROVIDER_ERROR(50001, HttpStatus.INTERNAL_SERVER_ERROR, "토큰 생성 중 에러가 발생하였습니다."),
    INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러가 발생하였습니다.");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;
}
