package com.ormi.mogakcote.exception.rate_limit;

import com.ormi.mogakcote.exception.dto.ErrorType;

public class DailyRateLimitExceededException extends RateLimitExceededException {

    public DailyRateLimitExceededException() {
        super(ErrorType.DAILY_RATE_LIMIT_EXCEEDED_ERROR);
    }

    public DailyRateLimitExceededException(String message) {
        super(ErrorType.RATE_LIMIT_EXCEEDED_ERROR, message);
    }

    public DailyRateLimitExceededException(ErrorType errorType, String customMessage) {
        super(errorType, customMessage);
    }
}
