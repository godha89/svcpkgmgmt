package com.pkg.mgmt.model;

public class BusinessException extends Exception {

	private static final long serialVersionUID = 1L;

	String exceptionMessage;

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public BusinessException(String exceptionMessage) {
		super();
		this.exceptionMessage = exceptionMessage;
	}

}
