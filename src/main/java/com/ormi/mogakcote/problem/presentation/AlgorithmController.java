package com.ormi.mogakcote.problem.presentation;

import static com.ormi.mogakcote.common.CrossOriginConstants.CROSS_ORIGIN_ADDRESS;

import com.ormi.mogakcote.problem.application.AlgorithmService;
import com.ormi.mogakcote.problem.dto.request.AlgorithmRequest;
import com.ormi.mogakcote.common.model.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = CROSS_ORIGIN_ADDRESS)
@RestController
@RequestMapping(path = "/api/v1/admin/algorithms")
@RequiredArgsConstructor
public class AlgorithmController {

    private final AlgorithmService algorithmService;

    //알고리즘 생성
    @PostMapping
    public ResponseEntity<?> createAlgorithm(
            @RequestBody @Valid AlgorithmRequest request) {
        var response = algorithmService.createAlgorithm(request);
        return ResponseDto.created(response);
    }

    //알고리즘 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAlgorithm(
            @PathVariable(name = "id") Long id,
            @RequestBody @Valid AlgorithmRequest request
    ) {
        var response = algorithmService.updateAlgorithm(id, request);
        return ResponseDto.ok(response);
    }

    //알고리즘 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAlgorithm(
            @PathVariable(name = "id")Long id
    ) {
        var response = algorithmService.deleteAlgorithm(id);
        return ResponseDto.ok(response);
    }

    //알고리즘 조회 ( 리스트로 )
    @GetMapping("/list")
    public ResponseEntity<?> algorithmList(){
        var response= algorithmService.getAlgorithmList();
        return ResponseDto.ok(response);
    }

}
