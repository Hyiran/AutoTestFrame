package com.atf.cap.api.exception;

import com.atf.cap.common.exception.CapException;

/**
 *
 *
 */
public class APIException extends CapException {

	public APIException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public APIException(String msg) {
		super(msg);
	}

	public APIException(Throwable cause) {
		super(cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5342703566853569975L;

}
