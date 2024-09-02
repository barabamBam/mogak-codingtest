package com.ormi.mogakcote.problem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LanguageResponse {
    private Long id;
    private String languageName;

    public static LanguageResponse toResponse(Long id, String languageName){
        return new LanguageResponse(id, languageName);
    }
}
