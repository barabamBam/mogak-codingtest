package com.ormi.mogakcote.exception.rate_limit;

import com.ormi.mogakcote.exception.dto.ErrorType;

public class PerSecondRateLimitExceededException extends RateLimitExceededException {

    public PerSecondRateLimitExceededException() {
        super(ErrorType.PER_SECOND_RATE_LIMIT_EXCEEDED_ERROR);
    }

    public PerSecondRateLimitExceededException(String message) {
        super(ErrorType.RATE_LIMIT_EXCEEDED_ERROR, message);
    }

    public PerSecondRateLimitExceededException(ErrorType errorType, String customMessage) {
        super(errorType, customMessage);
    }
}
