package com.pkg.mgmt.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class PackageMgmtErrorResponse {

	private String success;

	private String error;

	/**
	 * @param success
	 * @param error
	 */
	public PackageMgmtErrorResponse(String success, String error) {
		super();
		this.success = success;
		this.error = error;
	}

	public String getSuccess() {
		return success;
	}

	public String getError() {
		return error;
	}

}
