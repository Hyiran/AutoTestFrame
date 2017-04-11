package com.atf.cap.api.exception;

public class APIResponseException extends APIException {

	private final int statusCode;

	public APIResponseException(Integer code, String reason) {
		super(reason);
		this.statusCode = code;
	}

	@Override
	public String toString() {
		return "APIResponseException [statusCode=" + statusCode
				+ ", getMessage()=" + getMessage() + "]";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6790225967860003275L;

}
