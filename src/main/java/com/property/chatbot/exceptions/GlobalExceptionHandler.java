package com.property.chatbot.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<Error> handleAuthenticationException(HttpServletRequest request, AuthenticationException e) {
		log.error("Authentication error: {}", e.getMessage());
		Error error = ErrorUtils.createError(request, e.getMessage(), HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<Error> handleIllegalStateException(HttpServletRequest request, IllegalStateException e) {
		log.error("Invalid state: {}", e.getMessage());
		Error error = ErrorUtils.createError(request, e.getMessage(), HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}


	@ExceptionHandler(Exception.class)
	public ResponseEntity<Error> handleException(HttpServletRequest request, Exception e) {
		log.error("Unexpected error: {}", e.getMessage(), e);
		Error error = ErrorUtils.createError(request, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(APIException.class)
	public ResponseEntity<Error> apiException(HttpServletRequest request, APIException e) {
		String message = e.getMessage();
		Error error = ErrorUtils.createError(request, message, HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DataNotFoundException.class)
	public ResponseEntity<Error> dataNotFoundException(HttpServletRequest request, DataNotFoundException e) {
		String message = e.getMessage();
		Error error = ErrorUtils.createError(request, message, HttpStatus.NOT_FOUND.value());
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Error> handleValidationExceptions(HttpServletRequest request, MethodArgumentNotValidException e) {
		Map<String, String> errors = new HashMap<>();
		e.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		Error error = ErrorUtils.createError(request, "Field Validation failed", HttpStatus.BAD_REQUEST.value());
		error.errors = errors;
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
}
