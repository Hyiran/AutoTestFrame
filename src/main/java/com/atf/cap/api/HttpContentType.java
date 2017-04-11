package com.atf.cap.api;

import java.nio.charset.UnsupportedCharsetException;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.entity.ContentType;

/**
 * for some reasons Http tend to use ios-8859-1 encoding as default ,this is
 * just a convenient way for ctrip beginners,!
 * 
 * @author sqou
 *
 * @see ContentType
 */
public class HttpContentType {

	public static final ContentType APPLICATION_JSON_UTF_8 = ContentType.APPLICATION_JSON;
	// public static final ContentType APPLICATION_XML =
	// ContentType.APPLICATION_XML;

	public static final ContentType APPLICATION_XML_UTF_8 = ContentType.create(
			"application/xml", "UTF-8");

	public static final ContentType TEXT_HTML_UTF_8 = ContentType.create(
			"text/html", Consts.UTF_8);
	public static final ContentType TEXT_PLAIN_UTF_8 = ContentType.create(
			"text/plain", Consts.UTF_8);
	public static final ContentType TEXT_XML_UTF_8 = ContentType.create(
			"text/xml", Consts.UTF_8);

	public static ContentType getOrDefault(final HttpEntity entity)
			throws ParseException, UnsupportedCharsetException {
		ContentType contentType = ContentType.get(entity);
		return contentType == null ? TEXT_PLAIN_UTF_8 : contentType;
	}
}
