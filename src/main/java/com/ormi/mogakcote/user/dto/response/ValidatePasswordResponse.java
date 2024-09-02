package com.ormi.mogakcote.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ValidatePasswordResponse {
    @JsonProperty("isValid")
    private boolean isValid;
    private String[] errors;

    public ValidatePasswordResponse(boolean isValid, String error) {
        this.isValid = isValid;
        this.errors = error != null ? new String[]{error} : new String[]{};
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String[] getErrors() {
        return errors;
    }

    public void setErrors(String[] errors) {
        this.errors = errors;
    }
}