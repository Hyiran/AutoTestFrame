package com.atf.cap.api;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import com.atf.cap.api.exception.APIException;
import com.atf.cap.api.exception.APIResponseException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * 
 * @author sqou
 * @see Response
 */
public class APIResponse {

	private final HttpResponse response;

	private int statusCode;

	/**
	 * APIDriver has responsibillty to consume Response
	 */
	private boolean consumed;

	private String body;

	APIResponse(HttpResponse response) {
		this.response = response;
		statusCode = response.getStatusLine().getStatusCode();
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getHeaderValue(final String name) {
		return response.getFirstHeader(name).getValue();
	}

	public JsonElement readGsonElement() {
		innerConsumeContent();
		return new JsonParser().parse(body);
	}

	public <T> T readGsonEntity(Class<T> classOfT) {
		innerConsumeContent();
		return new Gson().fromJson(body, classOfT);
	}

	public String getBody() {
		innerConsumeContent();
		return body;
	}

	private void innerConsumeContent() {
		if (this.consumed) {
			return;
		}
		this.consumed = true;
		final StatusLine statusLine = response.getStatusLine();
		final HttpEntity entity = response.getEntity();
		if (statusLine.getStatusCode() >= 300) {
			throw new APIResponseException(statusLine.getStatusCode(),
					statusLine.getReasonPhrase());
		}
		if (entity != null) {
			try {
				this.body = EntityUtils.toString(entity);
			} catch (ParseException | IOException e) {
				throw new APIException(e);
			} finally {
				EntityUtils.consumeQuietly(entity);
			}
		}
	}

	public HttpResponse returnResponse() {
		assertNotConsumed();
		try {
			final HttpEntity entity = this.response.getEntity();
			if (entity != null) {
				final ByteArrayEntity byteArrayEntity = new ByteArrayEntity(
						EntityUtils.toByteArray(entity));
				final ContentType contentType = ContentType
						.getOrDefault(entity);
				byteArrayEntity.setContentType(contentType.toString());
				this.response.setEntity(byteArrayEntity);
			}
			return this.response;
		} catch (IOException e) {
			throw new APIException(e);
		}

		finally {
			this.consumed = true;
		}
	}

	public void discardContent() {
		dispose();
	}

	private void dispose() {
		if (this.consumed) {
			return;
		}
		try {
			final HttpEntity entity = this.response.getEntity();
			EntityUtils.consumeQuietly(entity);
		} catch (final Exception ignore) {
		} finally {
			this.consumed = true;
		}
	}

	private void assertNotConsumed() {
		if (this.consumed) {
			throw new IllegalStateException(
					"APIResponse content has been already consumed");
		}
	}
}
