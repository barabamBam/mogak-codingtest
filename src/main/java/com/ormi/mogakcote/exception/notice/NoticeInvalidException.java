package com.ormi.mogakcote.exception.notice;

import com.ormi.mogakcote.exception.BusinessException;
import com.ormi.mogakcote.exception.dto.ErrorType;

public class NoticeInvalidException extends BusinessException {
    public NoticeInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
