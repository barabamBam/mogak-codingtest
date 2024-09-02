package com.ormi.mogakcote.problem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlatformRequest {

    private Long platfromId;

    @NotBlank(message = "플랫폼 이름은 필수입니다.")
    private String platformName;
}
