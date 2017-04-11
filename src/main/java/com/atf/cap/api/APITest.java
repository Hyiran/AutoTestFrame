package com.atf.cap.api;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;
import java.util.TreeMap;

import com.atf.cap.internal.suite.APIJobSetUpListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import org.testng.xml.XmlTest;

import com.atf.cap.CapLogger;
import com.atf.cap.internal.data.DataMoude.DataServiceHolder;
import com.atf.cap.internal.domain.Context;
import com.atf.cap.internal.suite.ReporterSuiteListener;
import com.atf.cap.internal.suite.SuiteManListener;
import com.atf.cap.internal.test.MethodFilterChain;
import com.atf.cap.internal.util.NetUtils;
import com.atf.cap.kpi.domain.APIRun;
import com.atf.cap.lanucher.AppiumConfig.DriverType;
import com.atf.cap.lanucher.Config;
import com.atf.cap.reporter.CapReporter;
import com.atf.cap.template.TemplateRenderer;
import com.atf.cap.template.TemplateRendererBuilder;


/**
 * Currently build on Http 1.1 ,API test has many types,but Http stand in the
 * breach
 * 
 * relieves you from having to deal with connection management and resource
 * deallocation.
 * 
 * This Class doesn't hold good for all cases
 * 
 * @author sqou
 *
 */
@Listeners(value = { CapReporter.class, ReporterSuiteListener.class,
		APIJobSetUpListener.class })
public class APITest {

	private static final ThreadLocal<Context> contextHolder = new InheritableThreadLocal<>();
	protected static final Map<String, String> suiteParameters = new TreeMap<>(
			String.CASE_INSENSITIVE_ORDER);

	private static final Logger LOGGER = LoggerFactory.getLogger(APITest.class);

	protected static APIDriver apiDriver;

	protected APIResponse execute(APIRequest request) {
		APIRun apiRun = new APIRun();
		long current = System.currentTimeMillis();
		URI uri = request.getServiceURI();
		String requestType = uri.getScheme() + "://" + uri.getHost()
				+ uri.getPath();
		apiRun.setRequestURI(requestType);
		Long caseInfoId = contextHolder.get().getCaseInfo().getCaseInfoId();
		Long caseId = contextHolder.get().getCaseInfo().getCaseId();
		String ip = NetUtils.getLocalIp();
		apiRun.setCallerIp(ip);
		apiRun.setCaseInfoId(caseInfoId);
		apiRun.setCaseId(caseId);
		try {
			APIResponse response = apiDriver.execute(request);
			return response;
		} finally {
			long cost = System.currentTimeMillis() - current;
			apiRun.setActionTime(String.valueOf(cost));
			DataServiceHolder.avatarServiceClient().addAPIRun(apiRun);
		}

	}

	@BeforeSuite
	public void prepareHttpExecutor() {
		apiDriver = APIDriver.newInstance();
	}

	@AfterSuite
	public void releaseExecutor() {
	}

	@BeforeSuite
	public void setUpLogger(ITestContext context) {
		CapLogger.setContext(contextHolder);

		Config config = new Config();
		config.setDriverType(DriverType.HttpClient);
		config.setPlatformName("PC");

		Map<String, String> ps = context.getSuite().getXmlSuite()
				.getParameters();
		suiteParameters.putAll(ps);

		context.getSuite().setAttribute(SuiteManListener.CAP_CONFIG, config);

	}

	@BeforeSuite
	protected void prepareDataCenter() {
		TemplateRenderer render = TemplateRendererBuilder
				.createDefaultTemplateRenderer();
		TemplateRenderer.provideInstance(render);
	}

	@BeforeMethod(alwaysRun = true)
	public void setUpAPIContext(ITestContext testContext, Object[] parameters,
			Method method, XmlTest xmlTest) {

		LOGGER.info("before {}", method.getName());
		APIContext context = new APIContext();
		context.setTestContext(testContext);
		context.setXmlTest(xmlTest);
		context.setMethod(method);
		contextHolder.set(context);

		MethodFilterChain.beforeMethodAPI(context);
	}

	@AfterMethod(alwaysRun = true)
	public void destroyAPIContext(ITestContext testContext,
			ITestResult testResult, Method method) {

		APIContext context = (APIContext)contextHolder.get();
		context.setTestResult(testResult);

		MethodFilterChain.afterMethodAPI(context);

		contextHolder.remove();
	}

	protected String getSuiteParameter(String key) {
		return suiteParameters.get(key);
	}

}
