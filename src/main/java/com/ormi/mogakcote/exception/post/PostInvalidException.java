package com.ormi.mogakcote.exception.post;

import com.ormi.mogakcote.exception.BusinessException;
import com.ormi.mogakcote.exception.dto.ErrorType;

public class PostInvalidException extends BusinessException {

    public PostInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
