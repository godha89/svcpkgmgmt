package com.pkg.mgmt.Controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.pkg.mgmt.model.BusinessException;
import com.pkg.mgmt.model.PackageMgmtErrorResponse;

@ControllerAdvice
@RestController
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class })
	protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
		String bodyOfResponse = "Error Occured while managing package";
		PackageMgmtErrorResponse errorDetails = new PackageMgmtErrorResponse("false", bodyOfResponse);

		return handleExceptionInternal(ex, errorDetails, new HttpHeaders(), HttpStatus.CONFLICT, request);
	}

	@ExceptionHandler(value = { RestClientException.class })
	protected ResponseEntity<Object> handleRestClientExceptionConflict(RestClientException ex, WebRequest request) {
		String bodyOfResponse = "Error Occured while fetching products";
		PackageMgmtErrorResponse errorDetails = new PackageMgmtErrorResponse("false", bodyOfResponse);

		return handleExceptionInternal(ex, errorDetails, new HttpHeaders(), HttpStatus.CONFLICT, request);
	}

	@ExceptionHandler(value = { BusinessException.class })
	protected ResponseEntity<Object> handleBusinessExceptionConflict(BusinessException ex, WebRequest request) {
		PackageMgmtErrorResponse errorDetails = new PackageMgmtErrorResponse("false", ex.getExceptionMessage());

		return handleExceptionInternal(ex, errorDetails, new HttpHeaders(), HttpStatus.CONFLICT, request);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		PackageMgmtErrorResponse errorDetails = new PackageMgmtErrorResponse("false",
				ex.getBindingResult().getNestedPath());
		return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);
	}
}
