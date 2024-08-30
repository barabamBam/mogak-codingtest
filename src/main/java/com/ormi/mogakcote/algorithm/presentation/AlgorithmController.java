package com.ormi.mogakcote.algorithm.presentation;

import com.ormi.mogakcote.algorithm.application.AlgorithmService;
import com.ormi.mogakcote.algorithm.dto.request.AlgorithmRequest;
import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.common.model.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/admin/algorithm")
@RequiredArgsConstructor
public class AlgorithmController {

    private final AlgorithmService algorithmService;

    //알고리즘 생성
    @PostMapping
    public ResponseEntity<?> createAlgorithm(
            AuthUser user,
            @RequestBody @Valid AlgorithmRequest request) {
        var response = algorithmService.createAlgorithm(user.getId(), request);
        return ResponseDto.created(response);
    }

    //알고리즘 수정
    @PutMapping("{algorithmId}")
    public ResponseEntity<?> updateAlgorithm(
            @PathVariable Integer algorithmId,
            @RequestBody @Valid AlgorithmRequest request
    ) {
        var response = algorithmService.updateAlgorithm(algorithmId, request);
        return ResponseDto.ok(response);
    }

    //알고리즘 삭제
    @DeleteMapping("{algorithmId}")
    public ResponseEntity<?> deleteAlgorithm(
            @PathVariable Integer algorithmId
    ) {
        var response = algorithmService.deleteAlgorithm(algorithmId);
        return ResponseDto.ok(response);
    }

    //알고리즘 조회 ( 리스트로 )
    @GetMapping
    public ResponseEntity<?> algorithmList(){
        var response= algorithmService.getAlgorithmList();
        return ResponseDto.ok(response);
    }

}
