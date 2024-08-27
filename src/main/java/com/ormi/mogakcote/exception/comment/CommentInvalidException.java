package com.ormi.mogakcote.exception.comment;

import com.ormi.mogakcote.exception.BusinessException;
import com.ormi.mogakcote.exception.dto.ErrorType;

public class CommentInvalidException extends BusinessException {

    public CommentInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
