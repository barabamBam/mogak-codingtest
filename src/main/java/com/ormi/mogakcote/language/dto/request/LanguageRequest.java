package com.ormi.mogakcote.language.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LanguageRequest {

    private Integer languageId;

    @NotBlank(message = "언어 이름은 필수입니다.")
    private String languageName;
}
