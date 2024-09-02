package com.ormi.mogakcote.problem.presentation;

import com.ormi.mogakcote.common.model.ResponseDto;
import com.ormi.mogakcote.problem.application.PlatformServcie;
import com.ormi.mogakcote.problem.dto.request.PlatformRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/platforms")
@RequiredArgsConstructor
public class PlatformController {

    private final PlatformServcie platformServcie;

    // 플랫폼 생성
    @PostMapping
    public ResponseEntity<?> createPlatform(
//            AuthUser user,
            @RequestBody @Valid PlatformRequest request
    ) {
        var response = platformServcie.createPlatform(request);
        return ResponseDto.created(response);
    }

    // 플랫폼 수정
    @PutMapping("/{platformId}")
    public ResponseEntity<?> updatePlatform(
            @PathVariable("platformId") Long platformId,
            @RequestBody @Valid PlatformRequest request
    ) {
        var response = platformServcie.updatePlatform(platformId, request);
        return ResponseDto.ok(response);
    }

    // 플랫폼 삭제
    @DeleteMapping("/{platformId}")
    public ResponseEntity<?> deletePlatform(
            @PathVariable("platformId") Long platformId
    ) {
        var response = platformServcie.deletePlatform(platformId);
        return ResponseDto.ok(response);
    }


    // 플랫폼 리스트
    @GetMapping
    public ResponseEntity<?> getPlatformList(){
        var response = platformServcie.getPlatformList();
        return ResponseDto.ok(response);
    }
}
