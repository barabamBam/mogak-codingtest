package com.ormi.mogakcote.exception.badge;

import com.ormi.mogakcote.exception.BusinessException;
import com.ormi.mogakcote.exception.dto.ErrorType;

public class BadgeInvalidException extends BusinessException {
	public BadgeInvalidException(ErrorType errorType) { super(errorType); }
}
