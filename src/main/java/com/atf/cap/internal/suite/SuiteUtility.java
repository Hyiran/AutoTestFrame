package com.atf.cap.internal.suite;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.testng.ISuite;

import com.atf.cap.lanucher.Environment;
import com.sun.jna.platform.FileUtils;

/**
 * 
 * @author sqou
 *
 */
public class SuiteUtility {

	/**
	 * force update suite parameter
	 * 
	 * @param suite
	 * @param key
	 * @param value
	 */
	public static void updateParameter(ISuite suite, String key, String value) {
		Map<String, String> parameters = suite.getXmlSuite().getParameters();
		parameters.put(key, value);
		suite.getXmlSuite().setParameters(parameters);
	}

	/**
	 * for appium file naming coherence
	 * 
	 * @param serial
	 * @param app
	 * @return
	 */
	public static File constructFileName(String serial, String app) {
		String fileName = FilenameUtils.getName(app);
		serial = translateCode(serial);
		return new File(Environment.assertsCopyHome(), serial + "-" + fileName);
	}
	
	private static String translateCode(String serial){
		
		int hashcode = serial.hashCode();
		
		if(hashcode<0){
			return "m"+Math.abs(hashcode);
		}else{
			return ""+serial;
		}
		
	}
}
