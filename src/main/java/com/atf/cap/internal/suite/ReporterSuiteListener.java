package com.atf.cap.internal.suite;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import com.atf.cap.internal.performance.Profilers;
import com.atf.cap.reporter.SuiteResultWriter;

public class ReporterSuiteListener implements ISuiteListener {

	public static final String SUITE_START_DATE = "suiteStartDate";
	public static final String SUITE_END_DATE = "suiteEndDate";

	public static final String DEVICE_INFO = "DEVICE_INFO";
	public static final String APP_INFO = "APP_INFO";

	private static Logger logger = LoggerFactory
			.getLogger(ReporterSuiteListener.class);

	@Override
	public void onStart(ISuite suite) {
		logger.info("Order debug {}", Profilers.listenerOrder());

		suite.setAttribute(SUITE_START_DATE, new Date());
		prepareWorkspace(suite);
	}

	@Override
	public void onFinish(ISuite suite) {
		suite.setAttribute(SUITE_END_DATE, new Date());
	}

	private void prepareWorkspace(ISuite suite) {
		try {
			File suiteDir = new File(suite.getOutputDirectory());
			FileUtils.forceMkdir(suiteDir);
			File parent = suiteDir.getParentFile();
			File capReporter = new File(parent,
					SuiteResultWriter.REPORTER_DIRECTORY);
			File capCapture = new File(capReporter,
					SuiteResultWriter.REPORTER_IMAGES);

			FileUtils.forceMkdir(capCapture);

		} catch (Exception e2) {
			logger.error("prepare workspace directory", e2);
		}
	}
}
