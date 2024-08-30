package com.ormi.mogakcote.algorithm.application;

import com.ormi.mogakcote.algorithm.domain.Algorithm;
import com.ormi.mogakcote.algorithm.dto.request.AlgorithmRequest;
import com.ormi.mogakcote.algorithm.dto.response.AlgorithmResponse;
import com.ormi.mogakcote.algorithm.infrastructure.AlgorithmRepository;
import com.ormi.mogakcote.common.dto.SuccessResponse;
import com.ormi.mogakcote.exception.algorithm.AlgorithmInvalidException;
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
    public AlgorithmResponse createAlgorithm(Long id, AlgorithmRequest request) {
        Algorithm algorithm = buildAlgorithm(request, id);
        Algorithm savedAlgorithm = algorithmRepository.save(algorithm);

        return AlgorithmResponse.toResponse(
                savedAlgorithm.getAlgorithmId(),
                savedAlgorithm.getName()
        );
    }

    @Transactional
    public AlgorithmResponse updateAlgorithm(Integer algorithmId, AlgorithmRequest request) {
        throwsIfAlgorithmNotExist(algorithmId);
        Algorithm findAlgorithm = getAlgorithmById(algorithmId);

        findAlgorithm.update(request.getAlgorithmId(), request.getAlgorithmName());

        return AlgorithmResponse.toResponse(
                findAlgorithm.getAlgorithmId(),
                findAlgorithm.getName()
        );
    }


    @Transactional
    public SuccessResponse deleteAlgorithm(Integer algorithmId) {
        throwsIfAlgorithmNotExist(algorithmId);
        Algorithm findAlgorithm = getAlgorithmById(algorithmId);
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
    private Algorithm buildAlgorithm(AlgorithmRequest request, Long id) {
        return Algorithm.builder()
                .algorithmId(request.getAlgorithmId())
                .name(request.getAlgorithmName())
                .build();
    }

    private Algorithm getAlgorithmById(Integer algorithmId) {
        return  algorithmRepository.findByAlgorithmId(algorithmId).orElseThrow(
                () -> new AlgorithmInvalidException(ErrorType.ALGORITHM_NOTFOUND_ERROR)
        );
    }


    private void throwsIfAlgorithmNotExist(Integer algorithmId) {
        algorithmRepository.findByAlgorithmId(algorithmId).orElseThrow(
                () -> new AlgorithmInvalidException(ErrorType.ALGORITHM_NOTFOUND_ERROR));
    }
}
