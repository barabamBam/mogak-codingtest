package com.ormi.mogakcote.exception.problem;

import com.ormi.mogakcote.exception.BusinessException;
import com.ormi.mogakcote.exception.dto.ErrorType;

public class AlgorithmInvalidException extends BusinessException {
    public AlgorithmInvalidException(ErrorType errorType){
        super(errorType);
    }
}
