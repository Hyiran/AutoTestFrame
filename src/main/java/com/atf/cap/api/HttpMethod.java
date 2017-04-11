package com.atf.cap.api;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

/**
 * for new http user
 * 
 * @author sqou
 *
 */
public interface HttpMethod {
	/**
	 * HTTP GET method.
	 */
	public static final String GET = HttpGet.METHOD_NAME;
	/**
	 * HTTP POST method.
	 */
	public static final String POST = HttpPost.METHOD_NAME;
	/**
	 * HTTP PUT method.
	 */
	public static final String PUT = HttpPut.METHOD_NAME;
	/**
	 * HTTP DELETE method.
	 */
	public static final String DELETE = HttpDelete.METHOD_NAME;
	/**
	 * HTTP HEAD method.
	 */
	public static final String HEAD = HttpHead.METHOD_NAME;
	/**
	 * HTTP OPTIONS method.
	 */
	public static final String OPTIONS = HttpOptions.METHOD_NAME;
}
