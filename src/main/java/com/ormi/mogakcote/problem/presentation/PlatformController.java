package com.ormi.mogakcote.problem.presentation;

import static com.ormi.mogakcote.common.CrossOriginConstants.CROSS_ORIGIN_ADDRESS;

import com.ormi.mogakcote.common.model.ResponseDto;
import com.ormi.mogakcote.problem.application.PlatformService;
import com.ormi.mogakcote.problem.dto.request.PlatformRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = CROSS_ORIGIN_ADDRESS)
@RestController
@RequestMapping("/api/v1/admin/platforms")
@RequiredArgsConstructor
public class PlatformController {

    private final PlatformService platformServcie;

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
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlatform(
            @PathVariable("id") Long id,
            @RequestBody @Valid PlatformRequest request
    ) {
        var response = platformServcie.updatePlatform(id, request);
        return ResponseDto.ok(response);
    }

    // 플랫폼 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlatform(
            @PathVariable("id") Long id
    ) {
        var response = platformServcie.deletePlatform(id);
        return ResponseDto.ok(response);
    }


    // 플랫폼 리스트
    @GetMapping("/list")
    public ResponseEntity<?> getPlatformList(){
        var response = platformServcie.getPlatformList();
        return ResponseDto.ok(response);
    }
}
