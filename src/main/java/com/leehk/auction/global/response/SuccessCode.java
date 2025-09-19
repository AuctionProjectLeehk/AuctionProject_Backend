package com.leehk.auction.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    OK(20000, "요청이 성공적으로 처리되었습니다."),
    CREATED(20001, "정상적으로 생성되었습니다."),
    UPDATED(20002, "젖상적으로 수정되었습니다."),
    DELETED(20003, "정상적으로 삭제되었습니다.");

    private final int code;
    private final String message;
}
