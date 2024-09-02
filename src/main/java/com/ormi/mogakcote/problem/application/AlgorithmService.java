package com.ormi.mogakcote.problem.application;

import com.ormi.mogakcote.problem.domain.Algorithm;
import com.ormi.mogakcote.problem.dto.request.AlgorithmRequest;
import com.ormi.mogakcote.problem.dto.response.AlgorithmResponse;
import com.ormi.mogakcote.problem.infrastructure.AlgorithmRepository;
import com.ormi.mogakcote.common.dto.SuccessResponse;
import com.ormi.mogakcote.exception.problem.AlgorithmInvalidException;
import com.ormi.mogakcote.exception.dto.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlgorithmService {

    private final AlgorithmRepository algorithmRepository;

    @Transactional
    public AlgorithmResponse createAlgorithm(
            AlgorithmRequest request
    ) {
        Algorithm algorithm = buildAlgorithm(request);
        Algorithm savedAlgorithm = algorithmRepository.save(algorithm);

        return AlgorithmResponse.toResponse(
                savedAlgorithm.getAlgorithmId(),
                savedAlgorithm.getName()
        );
    }

    @Transactional
    public AlgorithmResponse updateAlgorithm(Long algorithmId, AlgorithmRequest request) {
        Algorithm findAlgorithm = getAlgorithmOrThrowIfNotExist(algorithmId);

        findAlgorithm.update(request.getAlgorithmName());

        return AlgorithmResponse.toResponse(
                findAlgorithm.getAlgorithmId(),
                findAlgorithm.getName()
        );
    }


    @Transactional
    public SuccessResponse deleteAlgorithm(Long algorithmId) {
        Algorithm findAlgorithm = getAlgorithmOrThrowIfNotExist(algorithmId);
        algorithmRepository.deleteByAlgorithmId(findAlgorithm.getAlgorithmId());

        return new SuccessResponse("알고리즘 삭제를 성공했습니다.");
    }

    @Transactional
    public List getAlgorithmList() {
        List<AlgorithmResponse> algorithmResponses = new ArrayList<>();
        List<Algorithm> findAlgorithms = algorithmRepository.findAll();

        findAlgorithms.forEach(findAlgorithm -> {
            algorithmResponses.add(AlgorithmResponse.toResponse(
                    findAlgorithm.getAlgorithmId(),
                    findAlgorithm.getName()
            ));
        });

        return algorithmResponses;
    }
    private Algorithm buildAlgorithm(AlgorithmRequest request) {
        return Algorithm.builder()
                .algorithmId(request.getAlgorithmId())
                .name(request.getAlgorithmName())
                .build();
    }

    private Algorithm getAlgorithmOrThrowIfNotExist(Long algorithmId) {
        return  algorithmRepository.findByAlgorithmId(algorithmId).orElseThrow(
                () -> new AlgorithmInvalidException(ErrorType.ALGORITHM_NOT_FOUND_ERROR)
        );
    }


//    private void throwsIfAlgorithmNotExist(Long algorithmId) {
//        algorithmRepository.findByAlgorithmId(algorithmId).orElseThrow(
//                () -> new AlgorithmInvalidException(ErrorType.ALGORITHM_NOT_FOUND_ERROR));
//    }
}
