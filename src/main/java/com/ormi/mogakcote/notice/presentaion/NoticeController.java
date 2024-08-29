package com.ormi.mogakcote.notice.presentaion;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.common.model.ResponseDto;
import com.ormi.mogakcote.notice.application.NoticeService;
import com.ormi.mogakcote.notice.dto.request.NoticeRequest;

import com.ormi.mogakcote.notice.dto.request.NoticeUpdateRequest;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

//     공지사항 생성
    @PostMapping
    public ResponseEntity<?> createNotice(
            AuthUser user,
            @RequestBody @Valid NoticeRequest request) {
        var response = noticeService.createNotice(user.getId(), request);
        return ResponseDto.created(response);
    }

//     공지사항 상세보기
    @GetMapping("/{noticeId}")
    public ResponseEntity<?> getNotice(
            AuthUser user,
            @PathVariable("noticeId") Long noticeId
    ) {
        var response = noticeService.getNotice(noticeId);
        return ResponseDto.ok(response);
    }

    // 공지사항 수정
    @PutMapping("/{noticeId}")
    public ResponseEntity<?> updateNotice(
            AuthUser user,
//            @PathVariable("adminId") Long adminId,
            @PathVariable("noticeId") Long noticeId,
            @RequestBody @Valid NoticeUpdateRequest request
    ) {
        var response = noticeService.updateNotice(noticeId, request);
        return ResponseDto.ok(response);
    }

    // 공지사항 삭제
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<?> deleteNotice(
            @PathVariable("noticeId") Long noticeId
    ) {
        var response = noticeService.deleteNotice(noticeId);
        return ResponseDto.ok(response);
    }

    @GetMapping("/noticeList")
    public ResponseEntity<?> noticeList() {
        var response = noticeService.getNoticeList();
        return ResponseDto.ok(response);
    }
}
