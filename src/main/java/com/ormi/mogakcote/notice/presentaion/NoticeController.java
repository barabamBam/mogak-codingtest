package com.ormi.mogakcote.notice.presentaion;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.common.model.ResponseDto;
import com.ormi.mogakcote.notice.application.NoticeService;
import com.ormi.mogakcote.notice.dto.request.NoticeRequest;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 공지사항 생성
    @PostMapping
    public ResponseEntity<?> createNotice(
            AuthUser user,
            @RequestBody @Valid NoticeRequest request)
    {
        var response = noticeService.createNotice(user.getId(), request);
        return ResponseDto.created(response);
    }

    // 공지사항 상세보기
    @GetMapping("/{id}")
    public ResponseEntity<?> getNotice(Long noticeId) {
        var response = noticeService.getNotice(noticeId);
        return ResponseDto.ok(response);
    }

    // 공지사항 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNotice(AuthUser user,
                                          @PathVariable("noticeId") Long noticeId,
                                          @RequestBody @Valid NoticeRequest request
    ) {
        var response = noticeService.updateNotice(user.getId(), noticeId, request);
        return ResponseDto.ok(response);
    }

    // 공지사항 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotice(AuthUser user,
                                          @PathVariable("noticeId") Long noticeId
    ) {
        var response = noticeService.deleteNotice(user.getId(), noticeId);
        return ResponseDto.ok(response);
    }
}
