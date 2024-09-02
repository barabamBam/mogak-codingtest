package com.ormi.mogakcote.problem.presentation;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.common.model.ResponseDto;
import com.ormi.mogakcote.problem.application.LanguageService;
import com.ormi.mogakcote.problem.dto.request.LanguageRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/languages")
@RequiredArgsConstructor
public class LanguageController {

    private final LanguageService languageService;

    // 작성 언어 생성
    @PostMapping
    public ResponseEntity<?> createLanguage(
            AuthUser user,
            @RequestBody @Valid LanguageRequest request
    ) {
        var response = languageService.createLanguage(request);
        return ResponseDto.created(response);
    }

    // 작성 언어 수정
    @PutMapping("/{languageId}")
    public ResponseEntity<?> updateLanguage(
            @PathVariable("languageId") Long languageId,
            @RequestBody @Valid LanguageRequest request
    ) {
        var response = languageService.updateLanguage(languageId, request);
        return ResponseDto.ok(response);
    }

    // 작성 언어 삭제
    @DeleteMapping("/{languageId}")
    public ResponseEntity<?> deleteLanguage(
            @PathVariable("languageId") Long languageId
    ) {
        var response = languageService.deleteLanguage(languageId);
        return ResponseDto.ok(response);
    }

    // 작성 언어 리스트 불러오기
    @GetMapping
    public ResponseEntity<?> getLanguageList(){
        var response = languageService.getLanguageList();
        return ResponseDto.ok(response);
    }
}
