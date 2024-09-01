package com.ormi.mogakcote.language.application;

import com.ormi.mogakcote.common.dto.SuccessResponse;
import com.ormi.mogakcote.exception.algorithm.AlgorithmInvalidException;
import com.ormi.mogakcote.exception.dto.ErrorType;
import com.ormi.mogakcote.exception.language.LanguageInvalidException;
import com.ormi.mogakcote.language.domain.Language;
import com.ormi.mogakcote.language.dto.request.LanguageRequest;
import com.ormi.mogakcote.language.dto.response.LanguageResponse;
import com.ormi.mogakcote.language.infrastructure.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageRepository languageRepository;

    @Transactional
    public LanguageResponse createLanguage(
            Long id,
            LanguageRequest request
    ) {
        Language language = buildLanguage(request, id);
        Language savedLanguage = languageRepository.save(language);

        return LanguageResponse.toResponse(
                savedLanguage.getLanguageId(),
                savedLanguage.getLanguageName()
        );
    }

    @Transactional
    public LanguageResponse updateLanguage(Integer languageId, LanguageRequest request) {
        throwsIfLanguageNotExist(languageId);
        Language findLanguage = getLanguageById(languageId);

        findLanguage.update(request.getLanguageName());

        return LanguageResponse.toResponse(
                findLanguage.getLanguageId(),
                findLanguage.getLanguageName()
        );
    }

    @Transactional
    public SuccessResponse deleteLanguage(Integer languageId) {
        throwsIfLanguageNotExist(languageId);
        Language findLanguage = getLanguageById(languageId);
        languageRepository.deleteByLanguageId(findLanguage.getLanguageId());

        return new SuccessResponse("작성언어 삭제를 성공했습니다.");
    }

    @Transactional
    public List getLanguageList() {
        List<LanguageResponse> languageResponses = new ArrayList<>();
        List<Language> findLanguages = languageRepository.findAll();

        findLanguages.forEach(findLanguage -> {
            languageResponses.add(LanguageResponse.toResponse(
                    findLanguage.getLanguageId(),
                    findLanguage.getLanguageName()
            ));
        });

        return languageResponses;
    }


    private Language buildLanguage(LanguageRequest request, Long id) {
        return Language.builder()
                .languageId(request.getLanguageId())
                .languageName(request.getLanguageName())
                .build();
    }

    private Language getLanguageById(Integer languageId) {
        return languageRepository.findByLanguageId(languageId).orElseThrow(
                () -> new LanguageInvalidException(ErrorType.LANGUAGE_NOT_FOUND_ERROR)
        );
    }

    private void throwsIfLanguageNotExist(Integer languageId) {
        languageRepository.findByLanguageId(languageId).orElseThrow(
                () -> new AlgorithmInvalidException(ErrorType.LANGUAGE_NOT_FOUND_ERROR));
    }
}
