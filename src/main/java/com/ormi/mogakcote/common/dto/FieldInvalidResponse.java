package com.ormi.mogakcote.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FieldInvalidResponse {

    private String errorCode;
    private String errorMessage;
}
