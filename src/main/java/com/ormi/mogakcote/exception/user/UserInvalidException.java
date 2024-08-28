package com.ormi.mogakcote.exception.user;

import com.ormi.mogakcote.exception.BusinessException;
import com.ormi.mogakcote.exception.dto.ErrorType;

public class UserInvalidException extends BusinessException {

    public UserInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
