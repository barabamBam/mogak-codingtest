package com.ormi.mogakcote.problem.presentation;

import static com.ormi.mogakcote.common.CrossOriginConstants.CROSS_ORIGIN_ADDRESS;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.common.model.ResponseDto;
import com.ormi.mogakcote.problem.application.LanguageService;
import com.ormi.mogakcote.problem.dto.request.LanguageRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = CROSS_ORIGIN_ADDRESS)
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
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLanguage(
            @PathVariable("id") Long id,
            @RequestBody @Valid LanguageRequest request
    ) {
        var response = languageService.updateLanguage(id, request);
        return ResponseDto.ok(response);
    }

    // 작성 언어 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLanguage(
            @PathVariable("id") Long id
    ) {
        var response = languageService.deleteLanguage(id);
        return ResponseDto.ok(response);
    }

    // 작성 언어 리스트 불러오기
    @GetMapping("/list")
    public ResponseEntity<?> getLanguageList(){
        var response = languageService.getLanguageList();
        return ResponseDto.ok(response);
    }
}
