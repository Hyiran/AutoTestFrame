package com.atf.cap.internal.suite;

import java.util.Map;
import java.util.Properties;

import org.testng.ISuite;
import org.testng.ISuiteListener;

import com.atf.cap.Constants;
import com.atf.cap.client.property.CapClientProperties;
import com.atf.cap.common.exception.CapException;
import com.atf.cap.common.utils.PropertyFileUtil;
import com.google.common.collect.Maps;

public class PropertiesSetUpListener implements ISuiteListener {

	@Override
	public void onFinish(ISuite arg0) {

	}

	@Override
	public void onStart(ISuite suite) {
		
		initCapClientSettings(Constants.CAP_CLIENT_PROPERTIES);

		updateCustomizedCapClientSettings(
				Constants.CAP_ENV_PROPERTIES);

		updateAppiumConfigByProperties(Constants.CAP_APPIUM_PROPERTIES, suite);

	}

	private void initCapClientSettings(String resource) {
		
		try {
			PropertyFileUtil.setSystemPropertiesFromPropFile(resource);
			
		} catch (Exception e) {

			throw new CapException("fail to init properties for cap client.");

		}
		
	}

	private void updateCustomizedCapClientSettings(String resource) {
		try {
			
			PropertyFileUtil.setSystemPropertiesFromPropFile(resource);
			
			
		} catch (Exception e) {

			throw new CapException("fail to update customized properties for from " + resource);

		}

	}

	private Map<String, String> updateAppiumConfigByProperties(String resource, ISuite suite) {

		Map<String, String> propertyConfig = Maps.newHashMap();

		try {
			propertyConfig = PropertyFileUtil
					.loadPropertiesAsMap(resource);
		} catch (Exception e) {

			throw new CapException(
					"fail to read properties from "
							+ Constants.CAP_APPIUM_PROPERTIES
							+ ". Please check whether both your test class and your config file are all under the src/test path. ");

		}

		Map<String, String> testngConfig = suite.getXmlSuite().getParameters();

		testngConfig.putAll(propertyConfig);

		suite.getXmlSuite().setParameters(testngConfig);

		return testngConfig;

	}

}
