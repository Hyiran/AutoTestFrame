package com.atf.cap.internal.domain;

import java.lang.reflect.Method;
import java.util.Map;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlTest;

/**
 * 
 * @author sqou
 *
 */
public class Context {

	private ITestContext testContext;
	private Method method;
	private ITestResult testResult;
	private XmlTest xmlTest;
	private CaseInfo caseInfo;

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public ITestContext getTestContext() {
		return testContext;
	}

	public void setTestContext(ITestContext testContext) {
		this.testContext = testContext;
	}

	public Map<String, String> getTestParamaters() {
		return xmlTest.getTestParameters();
	}

	public Map<String, String> getAllTestParamaters() {
		return xmlTest.getAllParameters();
	}

	public Map<String, String> getSuiteParamaters() {
		return testContext.getSuite().getXmlSuite().getParameters();
	}

	public XmlTest getXmlTest() {
		return xmlTest;
	}

	public void setXmlTest(XmlTest xmlTest) {
		this.xmlTest = xmlTest;
	}

	public ITestResult getTestResult() {
		return testResult;
	}

	public void setTestResult(ITestResult testResult) {
		this.testResult = testResult;
	}

	public CaseInfo getCaseInfo() {
		return caseInfo;
	}

	public void setCaseInfo(CaseInfo caseInfo) {
		this.caseInfo = caseInfo;
	}
}
