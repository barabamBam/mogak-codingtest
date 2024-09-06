package com.ormi.mogakcote.exception.rate_limit;

import com.ormi.mogakcote.exception.BusinessException;
import com.ormi.mogakcote.exception.dto.ErrorType;

public class RateLimitExceededException extends BusinessException {
    public RateLimitExceededException(ErrorType errorType) {
        super(errorType);
    }

    public RateLimitExceededException(String message) {
        super(ErrorType.RATE_LIMIT_EXCEEDED_ERROR, message);
    }

    public RateLimitExceededException(ErrorType errorType, String customMessage) {
        super(errorType, customMessage);
    }
}
