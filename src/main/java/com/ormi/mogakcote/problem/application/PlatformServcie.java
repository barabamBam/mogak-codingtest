package com.ormi.mogakcote.problem.application;

import com.ormi.mogakcote.common.dto.SuccessResponse;
import com.ormi.mogakcote.exception.dto.ErrorType;
import com.ormi.mogakcote.exception.problem.PlatformInvalidException;
import com.ormi.mogakcote.problem.domain.Platform;
import com.ormi.mogakcote.problem.dto.request.PlatformRequest;
import com.ormi.mogakcote.problem.dto.response.PlatformResponse;
import com.ormi.mogakcote.problem.infrastructure.PlatformRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class PlatformServcie {

    private final PlatformRepository platformRepository;

    @Transactional
    public PlatformResponse createPlatform(
            PlatformRequest request
    ) {
        Platform platform = buildPlatform(request);
        Platform savedPlatform = platformRepository.save(platform);

        return PlatformResponse.toResponse(
                savedPlatform.getPlatformId(),
                savedPlatform.getPlatformName()
        );
    }

    @Transactional
    public PlatformResponse updatePlatform(Long platformId, PlatformRequest request){
        Platform findPlatform = getPlatformOrThrowIfNotExist(platformId);

        findPlatform.update(request.getPlatformName());

        return PlatformResponse.toResponse(
                findPlatform.getPlatformId(),
                findPlatform.getPlatformName()
        );
    }

    @Transactional
    public SuccessResponse deletePlatform(Long platformId){
        Platform findPlatform = getPlatformOrThrowIfNotExist(platformId);
        platformRepository.deleteByPlatformId(findPlatform.getPlatformId());

        return new SuccessResponse("플랫폼 삭제를 성공했습니다.");
    }

    @Transactional
    public List getPlatformList(){
        List<PlatformResponse> platformResponses = new ArrayList<>();
        List<Platform> findPlatforms = platformRepository.findAll();

        findPlatforms.forEach(findPlatform -> {
            platformResponses.add(PlatformResponse.toResponse(
                    findPlatform.getPlatformId(),
                    findPlatform.getPlatformName()
            ));
        });
        return platformResponses;
    }

    private Platform buildPlatform(PlatformRequest request){
        return Platform.builder()
                .platformId(request.getPlatformId())
                .platformName(request.getPlatformName())
                .build();
    }

    private Platform getPlatformOrThrowIfNotExist(Long platformId){
        return platformRepository.findByPlatformId(platformId).orElseThrow(
                () -> new PlatformInvalidException(ErrorType.PLATFORM_NOT_FOUND_ERROR)
        );
    }
}
