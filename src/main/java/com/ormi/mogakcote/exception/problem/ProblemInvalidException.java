package com.ormi.mogakcote.exception.problem;

import com.ormi.mogakcote.exception.BusinessException;
import com.ormi.mogakcote.exception.dto.ErrorType;

public class ProblemInvalidException extends BusinessException {

    public ProblemInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
