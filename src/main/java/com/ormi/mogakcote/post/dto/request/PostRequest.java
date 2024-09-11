package com.ormi.mogakcote.post.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    private String content;

    @NotNull(message = "플랫폼 선택은 필수입니다.")
    private Long platformId;

    @Min(value = 1, message = "문제 번호 입력은 필수입니다.")
    @NotNull(message = "문제 번호 입력은 필수입니다.")
    private Integer problemNumber;

    @NotNull(message = "알고리즘 선택은 필수입니다.")
    private Long algorithmId;

    @NotNull(message = "언어 선택은 필수입니다.")
    private Long languageId;

    @NotBlank(message = "코드 입력은 필수입니다.")
    private String code;

    @JsonProperty("isPublic")
    private boolean isPublic;

    @JsonProperty("isReportRequested")
    private boolean isReportRequested;

    @JsonProperty("hasPreviousReportRequested")
    private boolean hasPreviousReportRequested;
}