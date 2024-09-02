package com.ormi.mogakcote.problem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LanguageResponse {
    private Long languageId;
    private String languageName;

    public static LanguageResponse toResponse(Long languageId, String languageName){
        return new LanguageResponse(languageId, languageName);
    }
}
