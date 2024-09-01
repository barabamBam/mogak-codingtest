package com.ormi.mogakcote.exception.language;

import com.ormi.mogakcote.exception.BusinessException;
import com.ormi.mogakcote.exception.dto.ErrorType;

public class LanguageInvalidException extends BusinessException {
    public LanguageInvalidException(ErrorType errorType){
        super(errorType);
    }
}
