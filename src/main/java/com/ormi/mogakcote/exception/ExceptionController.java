package com.ormi.mogakcote.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

@ControllerAdvice
public class ExceptionController {

	@GetMapping("/error")
	@ExceptionHandler(BusinessException.class)
	public String handleBusinessException(BusinessException e, Model model) {
		model.addAttribute("status", e.getErrorType().getStatus().value());
		model.addAttribute("error", e.getErrorType());
		model.addAttribute("message", e.getMessage());
		return "error";
	}
}