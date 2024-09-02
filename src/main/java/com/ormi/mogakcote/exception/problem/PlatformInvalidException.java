package com.ormi.mogakcote.exception.problem;

import com.ormi.mogakcote.exception.BusinessException;
import com.ormi.mogakcote.exception.dto.ErrorType;

public class PlatformInvalidException extends BusinessException {
    public PlatformInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
