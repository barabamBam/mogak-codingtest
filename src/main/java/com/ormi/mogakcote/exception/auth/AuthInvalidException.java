package com.ormi.mogakcote.exception.auth;

import com.ormi.mogakcote.exception.BusinessException;
import com.ormi.mogakcote.exception.dto.ErrorType;

public class AuthInvalidException extends BusinessException {

    public AuthInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
