package com.ormi.mogakcote.exception.news;

import com.ormi.mogakcote.exception.BusinessException;
import com.ormi.mogakcote.exception.dto.ErrorType;

public class NewsInvalidException extends BusinessException {

    public NewsInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
