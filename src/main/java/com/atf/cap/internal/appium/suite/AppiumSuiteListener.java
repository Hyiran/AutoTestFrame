package com.atf.cap.internal.appium.suite;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.exec.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import com.atf.cap.Constants;
import com.atf.cap.client.property.CapClientProperties;
import com.atf.cap.common.exception.CapException;
import com.atf.cap.common.utils.PropertyFileUtil;
import com.atf.cap.device.Device;
import com.atf.cap.device.DeviceStoreFactory;
import com.atf.cap.device.DeviceStoreFactory.MobilePlatformType;
import com.atf.cap.internal.appium.suite.android.AndroidStartupListener;
import com.atf.cap.internal.appium.suite.ios.IOSStartupListener;
import com.atf.cap.internal.appium.test.PrepareAppiumDriver;
import com.atf.cap.internal.performance.Profilers;
import com.atf.cap.internal.suite.JobSetUpListener;
import com.atf.cap.internal.suite.PropertiesSetUpListener;
import com.atf.cap.internal.suite.SuiteManListener;
import com.atf.cap.lanucher.AppiumConfig;
import com.atf.cap.lanucher.AppiumConfig.DriverType;
import com.atf.cap.lanucher.AppiumServer;
import com.atf.cap.lanucher.LinuxAppiumServer;
import com.atf.cap.lanucher.MacAppiumServer;
import com.atf.cap.lanucher.WinAppiumServer;
import com.google.common.collect.Maps;

/**
 * app uri download
 *
 * @author sqou
 * @see PrepareAppiumDriver
 */
public class AppiumSuiteListener implements ISuiteListener {

	static final Logger logger = LoggerFactory
			.getLogger(AppiumSuiteListener.class);
	
	protected PropertiesSetUpListener propertiesSetUpListener = new PropertiesSetUpListener();

	protected JobSetUpListener jobSetUpListener = new JobSetUpListener();

	private static final Map<Device, AppiumServer> appiumServers = new HashMap<>();

	// private static String REPORT_URL =
	// "http://autotest.sh.ctriptravel.com/Pages/Reports/CILogReport.aspx?JobID=";
	@Override
	public void onStart(ISuite suite) {

		logger.info("Order debug {}", Profilers.listenerOrder());
		
		propertiesSetUpListener.onStart(suite);
		
		logger.info("cap data server url is "+System.getProperty("capdata.server.url"));

		jobSetUpListener.onExecutionStart();
		// Get TestNG Config info
		Profilers.preAppium().start("resolveAppiumconfig");

		AppiumConfig config = new AppiumConfig(suite.getXmlSuite().getParameters());
		
		System.getProperty("capdata.server.url");

		logger.info("read appium config parameter from xmlsuite file\n{}",
				config);
		suite.setAttribute(SuiteManListener.CAP_CONFIG, config);

		config.validate();

		DriverType driverType = config.getDriverType();

		if (driverType.isAndroidPlatform()) {

			DeviceStoreFactory.create(MobilePlatformType.Android);
			ISuiteListener suiteListener = new AndroidStartupListener();
			suiteListener.onStart(suite);

			for (Device device : DeviceStoreFactory.getDeviceStore()
					.getDevices()) {
				if (OS.isFamilyDOS()) {
					appiumServers.put(device, new WinAppiumServer(device));
				} else {
					appiumServers.put(device, new LinuxAppiumServer(device));
				}
			}

		} else if (driverType.isIOSPlatform()) {
			DeviceStoreFactory.create(MobilePlatformType.iOS);
			IOSStartupListener ioslistener = new IOSStartupListener();
			ioslistener.onExecutionStart(suite);

			for (Device device : DeviceStoreFactory.getDeviceStore()
					.getDevices()) {
				appiumServers.put(device, new MacAppiumServer(device));
			}
		}

		logger.info("initial Run and Job info");

	}

	@Override
	public void onFinish(ISuite suite) {

		logger.info("suite:{} run finished", suite.getName());
		for (Device device : DeviceStoreFactory.getDeviceStore().getDevices()) {
			AppiumServer as = getAppiumServerWithDevice(device);
			if (as.isRunning()) {
				as.stopAppium();
			}
		}
		jobSetUpListener.onExecutionFinish();

	}



	public static AppiumServer getAppiumServerWithDevice(Device device) {
		return appiumServers.get(device);
	}

}
