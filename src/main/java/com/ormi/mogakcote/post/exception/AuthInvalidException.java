package com.ormi.mogakcote.post.exception;

import com.ormi.mogakcote.exception.dto.ErrorType;

public class AuthInvalidException extends RuntimeException {

    public AuthInvalidException(ErrorType errorType) {
        super(errorType.getMessage());
    }
}