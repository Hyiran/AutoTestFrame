package com.atf.cap;

import static org.testng.internal.EclipseInterface.ASSERT_LEFT;
import static org.testng.internal.EclipseInterface.ASSERT_RIGHT;
import static org.testng.internal.EclipseInterface.CLOSING_CHARACTER;
import static org.testng.internal.EclipseInterface.OPENING_CHARACTER;
import io.appium.java_client.AppiumDriver;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IClass;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;

import com.atf.cap.at.client.*;
import com.atf.cap.common.exception.NestedExceptionUtils;
import com.atf.cap.device.Device;
import com.atf.cap.framework.domain.LogCase;
import com.atf.cap.framework.domain.LogStep;
import com.atf.cap.internal.data.DataMoude.DataServiceHolder;
import com.atf.cap.internal.domain.CaseInfo;
import com.atf.cap.internal.domain.Context;
import com.atf.cap.reporter.SuiteResultWriter;
import com.atf.cap.ui.UIContext;

/**
 * CapLogger 将暴露为静态方法，方便使用，但同时也需要注意这个方法的使用上下文，在
 *
 */
public class CapLogger {

	public static final String SplitChar = "#";
	public static final String CaptureChar = "@";
	public static final Map<String, Object> filesMap = new ConcurrentHashMap<String, Object>();

	private static final FastDateFormat dateFormat = FastDateFormat
			.getInstance("yyyy-MM-dd HH:mm:ss");

	private static final Logger logger = LoggerFactory
			.getLogger(CapLogger.class);

	private static ThreadLocal<Context> contextHolder;

	private static final AtomicInteger captureIDGenerator = new AtomicInteger(1);

	private static AvatarServiceClient client = DataServiceHolder.avatarServiceClient();
	private static RunAtServiceClient runAtClient = DataServiceHolder
			.runAtServiceClient();

	public synchronized static void setContext(
			ThreadLocal<Context> contextHolder) {
		CapLogger.contextHolder = contextHolder;
	}

	public static void pass(String message) {
		logger.info(message);
		writeLog(LogCategory.Pass, message);
	}

	public static void pass(String message, String imagePath) {
		logger.info(message);
		writeLog(LogCategory.Pass, message, imagePath);
	}

	public static void error(String message) {
		logger.error(message);
		writeLog(LogCategory.Error, message);
	}

	public static void error(String message, Throwable throwable) {
		logger.error(message, throwable);
		if (throwable != null) {
			String detail = NestedExceptionUtils.buildStackTrace(throwable);
			message = message + ":" + detail;
		}
		writeLog(LogCategory.Error, message);
	}

	public static void error(String message, Throwable throwable,
			String imagePath) {
		logger.error("[[fk=Cap]] " + message, throwable);
		if (throwable != null) {
			String detail = NestedExceptionUtils.buildStackTrace(throwable);
			message = message + ":" + detail;
		}
		writeLog(LogCategory.Error, message, imagePath);
	}

	public static void step(String message) {
		logger.info(message);
		writeLog(LogCategory.Step, message);
	}

	public static void info(String message) {
		logger.info(message);
		writeLog(LogCategory.Info, message);
	}

	public static void info(String message, String imagePath) {
		logger.info(message);
		writeLog(LogCategory.Info, message, imagePath);
	}

	public static void remark(String message) {
		logger.info(message);
		message = StringUtils.trimToEmpty(message);
		writeToTestNG(LogCategory.Remark, message);
		writeRemarkToDatabase(message);
	}

	public static void logAPIInXML(String request, String response) {
		captureFileAsAttachment(request, response, FileType.xml);
	}

	public static void logAPIInJson(String request, String response) {
		captureFileAsAttachment(request, response, FileType.json);
	}

	public static void logAPIInText(String request, String response) {
		captureFileAsAttachment(request, response, FileType.text);
	}

	/**
	 * Ui 专用
	 * 
	 * @return guid;
	 * @throws IOException 
	 */
	public static String capture() throws IOException {
		File image = takeScreenshot();
		String guid;
		guid = runAtClient.uploadImgFileToServer(image.getAbsolutePath());
			if (StringUtils.isBlank(guid)) {
				logger.error("Upload image file to server fails. ");
			}
			return guid + ".png";
	}

	public static File captureToFile() {
		File image = takeScreenshot();
		String udid;
		udid = runAtClient
				.uploadImgFileToServer(image.getAbsolutePath());
		writeLog(LogCategory.Step, "图片比较", udid + ".png");
		return image;
	}

	private static File takeScreenshot() {
		Context context = contextHolder.get();
		
		ITestContext testContext = context.getTestContext();
		
		// mark this capture, for Exception Capture after test
		
		ITestResult result = context.getTestResult();
		String classMethodName = "";
		if (result != null) {
			IClass testClass = result.getTestClass();
			String className = testClass.getName();
			String methodName = result.getName();
			classMethodName = CaptureChar + className + "." + methodName;
		}

		String dir = testContext.getOutputDirectory();
		File suiteDir = new File(dir);
		File capRepoter = new File(suiteDir.getParent(),
				SuiteResultWriter.REPORTER_DIRECTORY);
		File screenDir = new File(capRepoter, SuiteResultWriter.REPORTER_IMAGES);

		String imageName = "p" + captureIDGenerator.incrementAndGet() + ".png";
		File image = new File(screenDir, imageName);

		
		if (context instanceof AppiumContext) {
			AppiumContext appiumContext = (AppiumContext) context;
			Device device = appiumContext.getDevice();

			try {
				AppiumDriver driver = appiumContext.getDriver();
			
				if (driver != null) {
					File tmp = driver.getScreenshotAs(OutputType.FILE);
					FileUtils.copyFile(tmp, image);

				} else {
					device.takeScreenshot(image.getAbsolutePath());
				}

			} catch (Exception e1) {
				logger.error("driver.getScreenshot", e1);
				try {
					device.takeScreenshot(image.getAbsolutePath());
				} catch (Exception e) {
					logger.warn("", e);
				}
			}
			
		}else if(context instanceof UIContext){
			UIContext uiContext= (UIContext) context;
			
			try {
				WebDriver driver = uiContext.getDriver();
			
				if (driver != null) {
					File tmp = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
					FileUtils.copyFile(tmp, image);

				} else {
					takeScreenshotByRobot(image);
				}

			} catch (Exception e1) {
				logger.error("driver.getScreenshot", e1);
				try {
					takeScreenshotByRobot(image);
				} catch (Exception e) {
					logger.warn("", e);
				}
			}
		}else{
			return null;
		}
		

		writeToTestNG(LogCategory.Captrue, SuiteResultWriter.REPORTER_IMAGES
				+ "/" + imageName + classMethodName);

		return image;
	}

	private static void takeScreenshotByRobot(File file) {
		Robot robot = null;
		try {  
			robot = new Robot();  
        } catch (AWTException e) {  
            System.err.println("Internal Error: " + e);  
            e.printStackTrace();  
        }  
		
		BufferedImage fullScreenImage = robot.createScreenCapture(new Rectangle(Toolkit  
                .getDefaultToolkit().getScreenSize()));  
		 try {
			ImageIO.write(fullScreenImage, "png", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/********************** private group 扯淡expect value ************/

	public static void pass(String expected, String actual, String message) {
		writeLog(LogCategory.Pass, expected, actual, message);
		writeToDatabase(LogCategory.Pass, expected, actual, message);
	}

	private static void captureFileAsAttachment(String request,
			String response, FileType fileType) {
		String requestName = "p" + captureIDGenerator.incrementAndGet();
		Map<String, String> formatFile = new HashMap<>();
		formatFile.put("format", fileType.name().toLowerCase());
		formatFile.put("payload", request);

		filesMap.put(requestName, formatFile);

		if (response != null) {
			String responseName = "p" + captureIDGenerator.incrementAndGet();
			requestName = requestName + ";" + responseName;
			Map<String, String> responseFile = new HashMap<>();
			responseFile.put("format", fileType.name().toLowerCase());
			responseFile.put("payload", response);

			filesMap.put(responseName, responseFile);
		}

		writeToTestNG(LogCategory.File, requestName);
	}

	private static void writeLog(LogCategory category, String message) {
		writeLog(category, message, null);
	}

	private static void writeLog(LogCategory category, String message,
			String imagePath) {
		message = StringUtils.trimToEmpty(message);
		writeToTestNG(category, message);
		writeToDatabase(category, "", "", message, imagePath);
	}

	private static void writeLog(LogCategory category, String expected,
			String actual, String message) {
		String formatted = "";
		if (null != message) {
			formatted = message + " ";
		}

		String fm = formatted + ASSERT_LEFT + expected + CLOSING_CHARACTER
				+ " and found " + OPENING_CHARACTER + actual + ASSERT_RIGHT;
		writeToTestNG(category, fm);
		writeToDatabase(category, expected, actual, message);
	}

	private static void writeToTestNG(LogCategory category, String message) {
		Reporter.log(String.format(category.toString() + "%s%s%s%s\n",
				SplitChar, message, SplitChar, dateFormat.format(new Date())));

	}

	private static void writeToDatabase(LogCategory category,
			String expectedValue, String actualValue, String message) {
		writeToDatabase(category, expectedValue, actualValue, message, null);
	}

	private static void writeToDatabase(LogCategory category,
			String expectedValue, String actualValue, String message,
			String imagePath) {
		CaseInfo caseInfo = contextHolder.get().getCaseInfo();

		LogStep logStep = new LogStep(caseInfo.getJobId().intValue(),
				category.getVal(), expectedValue, actualValue, message,
				new Date(), caseInfo.getCaseId().intValue());
		logStep.setImagePath(imagePath);

		client.addLogStep(logStep);
	}

	private static void writeRemarkToDatabase(String message) {
		CaseInfo caseInfo = contextHolder.get().getCaseInfo();
		LogCase logCase = new LogCase(caseInfo.getCaseId().intValue(), caseInfo
				.getJobId().intValue(), message);
		client.addRemark(logCase);

	}

	public enum LogCategory {

		Pass(0), Error(1), Info(0), Step(0), Remark(0), Captrue(0), File(0);

		private int val;

		private LogCategory(int val) {
			this.val = val;
		}

		public int getVal() {
			return val;
		}

	}

	enum FileType {
		xml, json, text;
	}
}
