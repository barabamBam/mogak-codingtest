package com.ormi.mogakcote.exception;

import com.ormi.mogakcote.exception.dto.ErrorType;

public class NewsInvalidException extends BusinessException {

    public NewsInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
