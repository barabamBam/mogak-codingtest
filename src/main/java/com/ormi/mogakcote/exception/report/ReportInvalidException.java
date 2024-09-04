package com.ormi.mogakcote.exception.report;

import com.ormi.mogakcote.exception.BusinessException;
import com.ormi.mogakcote.exception.dto.ErrorType;

public class ReportInvalidException extends BusinessException {

    public ReportInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
