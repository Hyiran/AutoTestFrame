package com.atf.cap.internal.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atf.cap.AppiumContext;
import com.atf.cap.internal.appium.test.ExceptionHandler;
import com.atf.cap.internal.appium.test.PrepareAppiumDriver;
import com.atf.cap.internal.appium.test.PrepareDevice;
import com.atf.cap.internal.appium.test.PrepareIOSDriver;
import com.atf.cap.internal.appium.test.PrepareWebDriver;
import com.atf.cap.internal.domain.Context;
import com.atf.cap.lanucher.AppiumConfig.DriverType;

/**
 * 
 * @author sqou
 *
 */
public class MethodFilterChain {

	static final Logger logger = LoggerFactory
			.getLogger(MethodFilterChain.class);

	private static final List<? extends MethodFilter> androidBeforeFilters = Arrays
			.asList(new PrepareDevice(), new PrepareCaseInfo(),
					new PrepareAppiumDriver());

	private static final List<? extends MethodFilter> iosBeforeFilters = Arrays
			.asList(new PrepareDevice(), new PrepareCaseInfo(),
					new PrepareIOSDriver());

	private static final List<? extends MethodFilter> androidAfterFilters = Arrays
			.asList(new ExceptionHandler(), new PostActionCaseInfo());

	private static final List<? extends MethodFilter> iosAfterFilters = Arrays
			.asList(new ExceptionHandler(), new PostActionCaseInfo());

	private static final List<? extends MethodFilter> apiBeforeFilters = Arrays
			.asList(new PrepareCaseInfo());
	
	private static final List<? extends MethodFilter> uiBeforeFilters = Arrays
			.asList(new PrepareCaseInfo(),new PrepareWebDriver());

	private static final List<? extends MethodFilter> apiAfterFilters = Arrays.
			asList(new PostActionCaseInfo());

	private static final List<? extends MethodFilter> uiAfterFilters = Arrays.
			asList(new ExceptionHandler(), new PostActionCaseInfo());

	public static void beforeMethod(Context context) {
		AppiumContext ac = (AppiumContext) context;
		DriverType dt = ac.getAppiumConfig().getDriverType();

		List<? extends MethodFilter> beforeFilters = new ArrayList<MethodFilter>();
		if (dt.isAndroidPlatform()) {
			beforeFilters = androidBeforeFilters;
		} else if (dt.isIOSPlatform()) {
			beforeFilters = iosBeforeFilters;
		}

		for (MethodFilter filter : beforeFilters) {
			filter.doFilter(context);
		}
	}

	public static void afterMethod(Context context) {
		AppiumContext ac = (AppiumContext) context;
		DriverType dt = ac.getAppiumConfig().getDriverType();

		List<? extends MethodFilter> afterFilters = new ArrayList<MethodFilter>();

		if (dt.isAndroidPlatform()) {
			afterFilters = androidAfterFilters;
		} else if (dt.isIOSPlatform()) {
			afterFilters = iosAfterFilters;
		}

		for (MethodFilter filter : afterFilters) {

			try {
				filter.doFilter(context);
			} catch (Exception e) {
				logger.warn("afterMethod", e);
			}
		}
	}

	public static void beforeMethodAPI(Context context) {
		for (MethodFilter filter : apiBeforeFilters) {
			filter.doFilter(context);
		}
	}

	public static void afterMethodAPI(Context context) {
		for (MethodFilter filter : apiAfterFilters) {
			filter.doFilter(context);
		}
	}
	
	public static void beforeMethodUI(Context context) {
		for (MethodFilter filter : uiBeforeFilters) {
			filter.doFilter(context);
		}
	}

	public static void afterMethodUI(Context context) {
		for (MethodFilter filter : uiAfterFilters) {
			filter.doFilter(context);
		}
	}
}
