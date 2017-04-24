/**
 * 
 */
package com.atf.cap.internal.suite;

import com.atf.cap.common.exception.CapException;
import com.atf.cap.common.utils.PropertyFileUtil;
import com.atf.cap.internal.util.HostUtils;
import com.atf.cap.lanucher.AppiumConfig.DriverType;
import com.atf.cap.lanucher.Environment;
import com.atf.cap.lanucher.UIConfig;
import com.atf.cap.ui.UITest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;


/**
 * @author sqou
 *
 */
public class UISuiteListener implements ISuiteListener {

	static final Logger logger = LoggerFactory
			.getLogger(UISuiteListener.class);
	
	@Override
	public void onStart(ISuite suite) {
		logger.info("read testng config parameter from xmlsuite file\n{}",suite);
		Map<String, String> testngConfig = suite.getXmlSuite().getParameters();
		UIConfig config = new UIConfig(testngConfig);
		
		config.setDriverType(DriverType.HttpClient);
		config.setPlatformName("PC");
		
		overriteTestngConfig(config,suite);
		suite.setAttribute(SuiteManListener.CAP_CONFIG, config);
		config.validate();
				
		logger.info("read host config parameter from dns.properties file\n{}",suite);
		try {
			Properties props = PropertyFileUtil.loadProperties("/env/dns.properties");
			HostUtils.initHostFile(props);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.info("read env config parameter from properties file\n{}",suite);
		String env;
		if (testngConfig.containsKey("env")) {
			env = testngConfig.get("env").toLowerCase();
		} else {
			env = "uat";
		}
		
		String path = "/env/" +env + ".properties";	
		
		try {			
			UITest.envData = PropertyFileUtil
					.loadPropertiesAsMap(path);
		} catch (Exception e) {

			throw new CapException(
					"fail to read evn properties from "
							+ path
							+ ". Please make sure this file is correct. ");

		}
	}

	@Override
	public void onFinish(ISuite suite) {
		try {
			HostUtils.resumeHostfile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	 private void overriteTestngConfig(UIConfig config, ISuite suite) {

	        if (Environment.isLab()) {
	            String env = System.getenv("env");
	            if (StringUtils.isNotBlank(env)) {
	                config.setEnv(env);
	                SuiteUtility.updateParameter(suite, "env", env);
	            }

	            String browserType = System.getenv("browserType");
	            if (StringUtils.isNotBlank(browserType)) {
	                config.setBrowserType(browserType);
	                SuiteUtility.updateParameter(suite, "browserType", browserType);
	            }
	            
	            String proxy = System.getenv("proxy");
	            if (StringUtils.isNotBlank(proxy)) {
	                config.setProxy(proxy);
	                SuiteUtility.updateParameter(suite, "proxy", proxy);
	            }
	        }
	   }
}
