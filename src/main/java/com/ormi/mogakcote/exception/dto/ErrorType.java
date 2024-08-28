package com.ormi.mogakcote.exception.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorType {

    CONFLICT_ERROR(HttpStatus.BAD_REQUEST, "예기치 못한 에러가 발생했습니다."),

    // report 예외
    PROBLEM_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST, "요구에 맞는 문제가 존재하지 않습니다. 플랫폼과 문제 번호를 확인해주세요."),

    // post 예외
    POST_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),

    // comment 예외
    COMMENT_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),

    // auth 예외
    WRONG_PASSWORD_ERROR(HttpStatus.UNAUTHORIZED, "잘못된 비밀번호입니다"),
    NON_IDENTICAL_USER_ERROR(HttpStatus.BAD_REQUEST, "작성자와 접근자가 동일하지 않습니다."),
    INVALID_ACCESS_TOKEN_ERROR(HttpStatus.FORBIDDEN, "잘못된 액세스 토큰입니다"),
    USED_ACCESS_TOKEN_ERROR(HttpStatus.FORBIDDEN, "이미 사용된 엑세스 토큰입니다"),
    USED_REFRESH_TOKEN_ERROR(HttpStatus.FORBIDDEN, "이미 사용된 리프레시 토큰입니다"),
    REFRESH_ACCESS_TOKEN_NOT_MATCH_ERROR(HttpStatus.FORBIDDEN, "엑세스 토큰이 일치하지 않습니다"),

    // user 예외
    USER_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다");

    private final HttpStatus status;
    private final String message;
}
