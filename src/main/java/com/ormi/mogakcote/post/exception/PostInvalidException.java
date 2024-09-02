package com.ormi.mogakcote.post.exception;

import com.ormi.mogakcote.exception.dto.ErrorType;

public class PostInvalidException extends RuntimeException {

    public PostInvalidException(ErrorType errorType) {
        super(errorType.getMessage());
    }
}