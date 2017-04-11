package com.atf.cap.api;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.atf.cap.api.exception.APIException;
import com.google.gson.GsonBuilder;

/**
 * The Default Chareset is UTF-8
 * 
 * @author sqou
 *
 * @see HttpContentType
 */
public class APIRequest {

	private URI serviceURI;
	private String body;

	private final HttpUriRequest httpRequest;

	public static APIRequest get(final URI uri) {
		return new APIRequest(new HttpGet(uri));
	}

	public static APIRequest get(final String uri) {
		return new APIRequest(new HttpGet(uri));
	}

	public static APIRequest post(final URI uri) {
		return new APIRequest(new HttpPost(uri));
	}

	public static APIRequest post(final String uri) {
		return new APIRequest(new HttpPost(uri));
	}

	public static APIRequest put(final URI uri) {
		return new APIRequest(new HttpPut(uri));
	}

	public static APIRequest put(final String uri) {
		return new APIRequest(new HttpPut(uri));
	}

	public static APIRequest delete(final URI uri) {
		return new APIRequest(new HttpDelete(uri));
	}

	public static APIRequest delete(final String uri) {
		return new APIRequest(new HttpDelete(uri));
	}

	public static APIRequest head(final URI uri) {
		return new APIRequest(new HttpHead(uri));
	}

	public static APIRequest head(final String uri) {
		return new APIRequest(new HttpHead(uri));
	}

	APIRequest(HttpUriRequest httpRequest) {
		super();
		this.serviceURI = httpRequest.getURI();
		this.httpRequest = httpRequest;
	}

	public APIRequest addHeader(final String name, final String value) {
		httpRequest.addHeader(name, value);
		return this;
	}

	public APIRequest setHeader(final String name, final String value) {
		httpRequest.setHeader(name, value);
		return this;
	}

	public APIRequest removeHeader(final String name) {
		httpRequest.removeHeaders(name);
		return this;
	}

	public APIRequest userAgent(final String agent) {
		this.httpRequest.setHeader(HTTP.USER_AGENT, agent);
		return this;
	}

	public APIRequest body(final HttpEntity entity) {
		if (this.httpRequest instanceof HttpEntityEnclosingRequest) {
			((HttpEntityEnclosingRequest) this.httpRequest).setEntity(entity);
		} else {
			throw new IllegalStateException(this.httpRequest.getMethod()
					+ " request cannot enclose an entity");
		}
		if (entity instanceof StringEntity) {
			try {
				this.body = EntityUtils.toString(entity);
			} catch (ParseException | IOException e) {
				throw new APIException(e);
			}
		}
		return this;
	}

	public APIRequest bodyJsonEntity(String body) {
		body(new StringEntity(body, HttpContentType.APPLICATION_JSON_UTF_8));
		return this;
	}

	public APIRequest bodyXmlEntity(String body) {
		body(new StringEntity(body, HttpContentType.APPLICATION_XML_UTF_8));
		return this;
	}

	public APIRequest bodyGsonEntity(Object object) {
		String body = new GsonBuilder().create().toJson(object);
		body(new StringEntity(body, HttpContentType.APPLICATION_JSON_UTF_8));
		return this;
	}

	public APIRequest bodyFile(final File file, final ContentType contentType) {
		return body(new FileEntity(file, contentType));
	}

	public APIRequest bodyForm(
			final Iterable<? extends NameValuePair> formParams) {
		return bodyForm(formParams, Consts.UTF_8);
	}

	public APIRequest bodyForm(
			final Iterable<? extends NameValuePair> formParams,
			final Charset charset) {
		final List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		for (NameValuePair param : formParams) {
			paramList.add(param);
		}
		final ContentType contentType = ContentType.create(
				URLEncodedUtils.CONTENT_TYPE, charset);
		final String s = URLEncodedUtils.format(paramList,
				charset != null ? charset.name() : null);
		body(new StringEntity(s, contentType));
		return this;
	}

	/**
	 * @see HttpContentType
	 * @param body
	 * @param contentType
	 * @return
	 */
	public APIRequest body(String body, ContentType contentType) {
		body(new StringEntity(body, contentType));
		return this;
	}

	/**
	 * for internal usage
	 * 
	 * @return
	 */
	public String getBody() {
		return body;
	}

	public URI getServiceURI() {
		return serviceURI;
	}

	HttpUriRequest getRequest() {
		return httpRequest;
	}

}
