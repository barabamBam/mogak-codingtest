package com.ormi.mogakcote.notice.presentaion;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.common.model.ResponseDto;
import com.ormi.mogakcote.notice.application.NoticeService;
import com.ormi.mogakcote.notice.domain.Notice;
import com.ormi.mogakcote.notice.dto.request.NoticeRequest;

import com.ormi.mogakcote.notice.dto.request.NoticeUpdateRequest;
import com.ormi.mogakcote.notice.dto.response.NoticeResponse;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping(path = "/api/v1/notice")
@RequiredArgsConstructor
@Slf4j
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
    public ModelAndView getNotice(
        @PathVariable("noticeId") Long noticeId,
        Model model
    ) {
        NoticeResponse notice = noticeService.getNotice(noticeId);
        model.addAttribute("notice", notice);
        return new ModelAndView("notice/detail");
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

    @GetMapping("/{noticeId}/edit")
    public ModelAndView showEditForm(@PathVariable("noticeId") Long noticeId, Model model) {
        Notice noticeResponse = noticeService.getNoticeById(noticeId);
        if (noticeResponse == null) {
            return new ModelAndView("admin/adminPageHtml");
        }
        model.addAttribute("notice", noticeResponse);
        return new ModelAndView("notice/edit");  // 이는 수정 페이지의 Thymeleaf 템플릿 이름입니다.
    }

    // 공지사항 삭제
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<?> deleteNotice(
        @PathVariable("noticeId") Long noticeId
    ) {
        var response = noticeService.deleteNotice(noticeId);
        return ResponseDto.ok(response);
    }

    //    @GetMapping("/noticeList")
    //    public ResponseEntity<?> noticeList() {
    //        var response = noticeService.getNoticeList();
    //        return ResponseDto.ok(response);
    //    }

    @GetMapping("/latest5List")
    public ModelAndView noticeLatest5List(
        Model model
    ) {
        List<NoticeResponse> responses = noticeService.getNoticeLatestFive();
        model.addAttribute("noticeLatest5List", responses);
        return new ModelAndView( "admin/adminPageHtml");
    }

    @GetMapping("/create")
    public ModelAndView noticeCreateForm(
        Model model
    ){
        return new ModelAndView("notice/create");
    }
}