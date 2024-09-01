package com.ormi.mogakcote.language.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LanguageResponse {
    private Integer languageId;
    private String languageName;

    public static LanguageResponse toResponse(Integer languageId, String languageName){
        return new LanguageResponse(languageId, languageName);
    }
}
