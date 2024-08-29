package com.ormi.mogakcote.RegisterUser.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegisterResponse {
    @JsonProperty("userId")
    private String userId;
    private String message;

    public RegisterResponse(String userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}