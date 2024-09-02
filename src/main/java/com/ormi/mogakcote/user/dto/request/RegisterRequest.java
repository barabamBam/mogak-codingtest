package com.ormi.mogakcote.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    @JsonProperty("confirmPassword")
    private String confirmPassword;
    private String nickname;
}