package com.atf.cap.dataprovider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 
 * @author sqou
 *
 */
@XStreamAlias("testData")
public class TestData implements DataParameter {

	private TestMeta testMeta;

	private Object payload;

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

	public String playloadJson() {
		return new Gson().toJson(payload);
	}

	public TestMeta getTestMeta() {
		return testMeta;
	}

	public void setTestMeta(TestMeta testMeta) {
		this.testMeta = testMeta;
	}

	public String payloadJson() {
		return new GsonBuilder().create().toJson(payload);
	}

	@Override
	public String title() {
		return testMeta.getTitle();
	}

}
