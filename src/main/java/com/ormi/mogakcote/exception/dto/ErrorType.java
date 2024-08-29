package com.ormi.mogakcote.exception.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    CONFLICT_ERROR(HttpStatus.BAD_REQUEST, "예기치 못한 에러가 발생했습니다."),

//     report 예외
    PROBLEM_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST, "요구에 맞는 문제가 존재하지 않습니다. 플랫폼과 문제 번호를 확인해주세요."),

    // post 예외
    POST_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),

    // comment 예외
    COMMENT_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),

    // auth 예외
    NON_IDENTICAL_USER_ERROR(HttpStatus.BAD_REQUEST, "작성자와 접근자가 동일하지 않습니다."),

    // notice 예외
    NOTICE_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "공지사항을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
