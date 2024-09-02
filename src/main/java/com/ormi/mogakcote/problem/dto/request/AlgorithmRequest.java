package com.ormi.mogakcote.problem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AlgorithmRequest {

    private Long algorithmId;
    @NotBlank(message = "알고리즘 이름은 필수입니다.")
    private String algorithmName;
}
