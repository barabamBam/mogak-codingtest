package com.ormi.mogakcote.exception.auth;

import com.ormi.mogakcote.exception.BusinessException;
import com.ormi.mogakcote.exception.dto.ErrorType;

public class UserAuthManagementInvalidException extends BusinessException {

    public UserAuthManagementInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
