package com.atf.cap.internal.suite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

/**
 * just for holding cap_config, need Refactoring
 * 
 * @author sqou
 *
 */
public class SuiteManListener implements ISuiteListener {

	static final Logger logger = LoggerFactory
			.getLogger(SuiteManListener.class);

	public static final String CAP_CONFIG = "capConfig";

	@Override
	public void onStart(ISuite suite) {

	}

	@Override
	public void onFinish(ISuite suite) {
	}

}
