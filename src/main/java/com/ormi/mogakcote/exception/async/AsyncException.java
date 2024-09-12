package com.ormi.mogakcote.exception.async;

import com.ormi.mogakcote.exception.BusinessException;
import com.ormi.mogakcote.exception.dto.ErrorType;

public class AsyncException extends BusinessException {

    public AsyncException(ErrorType errorType) {
        super(errorType);
    }
}
