package com.ormi.mogakcote.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FindEmailRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String nickname;
}
