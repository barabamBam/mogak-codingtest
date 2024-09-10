package com.ormi.mogakcote.news.presentation;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.common.model.ResponseDto;
import com.ormi.mogakcote.news.application.NewsService;
import com.ormi.mogakcote.news.dto.request.NewsRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    /**
     * 알림 생성: 관리자 또는 시스템만 생성 가능하다.
     */
    @PostMapping
    public ResponseEntity<?> createNews(
            @RequestBody @Valid NewsRequest request
    ) {
        var response = newsService.createNews(request);
        return ResponseDto.created(response);
    }

    /**
     * 알림 조회: 당사자만 조회 가능하다.
     */
    @GetMapping(path = "/{newsId}")
    public ResponseEntity<?> getNews(
            AuthUser user,
            @PathVariable(name = "newsId") Long newsId
    ) {
        var response = newsService.getNews(user, newsId);
        return ResponseDto.ok(response);
    }

    /**
     * 알림 수정: 관리자만 수정 가능하다.
     */
    @PutMapping(path = "/{newsId}")
    public ResponseEntity<?> updateNews(
            AuthUser user,
            @PathVariable(name = "newsId") Long newsId,
            @RequestBody @Valid NewsRequest request
    ) {
        var response = newsService.updateNews(user, newsId, request);
        return ResponseDto.ok(response);
    }

    /**
     * 알림 삭제: 관리자만 삭제 가능하다.
     */
    @DeleteMapping(path = "/{newsId}")
    public ResponseEntity<?> deleteNews(
            AuthUser user,
            @PathVariable(name = "newsId") Long newsId
    ) {
        var response = newsService.deleteNews(user, newsId);
        return ResponseDto.ok(response);
    }

    /**
     * 전체 알림 목록
     */
    @GetMapping(path = "/list")
    public ResponseEntity<?> getAllNews(
            AuthUser user,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        var responses = newsService.getAllNews(user, pageable);
        return ResponseDto.ok(responses);
    }

}
