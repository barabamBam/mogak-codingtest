package com.ormi.mogakcote.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class AccessTokenWrapper implements Serializable {

    @JsonProperty("access_token")
    @NotBlank
    private String accessToken;
}
