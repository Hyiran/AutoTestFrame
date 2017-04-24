package com.atf.cap.internal.test;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IMethodSelector;
import org.testng.IMethodSelectorContext;
import org.testng.ITestNGMethod;

import com.atf.cap.lanucher.Environment;

public class TestMethodSelector implements IMethodSelector {

	/**
	 * Keep version compatibility
	 */
	private static final long serialVersionUID = 4166247968392649912L;
	private static final Logger logger = LoggerFactory
			.getLogger(TestMethodSelector.class);
	

	/**
	 * Filter Method by Group and Priority 
	 */
	@Override
	public boolean includeMethod(IMethodSelectorContext context,
			ITestNGMethod method, boolean isTestMethod) {
		
		// Under local env, do not filter method, running test cases depend on testng.xml
//		if (!Environment.isLab()){
//			logger.info("On local environment.");
//			return true;
//		}
		
		
		// if method is not test, return false
		if(!method.isTest()){
			return false;
		}

		// Get Jenkins env
		// jenkins do not set group or priority, the default methods depend on testng.xml
		String priority = System.getenv("Priority");
		String groupenv = System.getenv("Group");
		if((priority == null || priority.isEmpty()) && (groupenv == null || groupenv.isEmpty())){
			return true;
		}

		List<String> priorityArray = null;
		List<String> categoryArray = null;
		if(priority != null &&  !priority.isEmpty()){
			logger.info("Jenkins Priority env: " + priority);
			priorityArray = Arrays.asList(priority.split(";"));
		}
				
		if(groupenv != null &&  !groupenv.isEmpty()){
			logger.info("Jenkins Group env: " + groupenv);
			categoryArray = Arrays.asList(groupenv.split(";"));
		}
			
		// Filter test methods When setting group and priority or only setting group
		// Setting group and priority: Filter test methods when group and priority are exist
		logger.info("Method name: " + method.getMethodName() + " *** Method is test: " + method.isTest()
				+ " *** Group name: " + stringArrayToString(method.getGroups()) + "*** priority: " + method.getPriority());
		if(categoryArray != null && categoryArray.size() > 0){
			for (String group : method.getGroups()) {
				if(priorityArray != null && priorityArray.size() > 0){
					int currentPriority = method.getPriority();
					if(categoryArray.contains(group) && priorityArray.contains(Integer.toString(currentPriority))){
						context.setStopped(true);
						return true;
					}
				}else{
					if(categoryArray.contains(group)){
						context.setStopped(true);
						return true;
					}
				}
				
			}			
		}
		
		// Filter test methods When setting priority
		if(priorityArray != null && priorityArray.size() > 0){
			int currentPriority = method.getPriority();
			if(priorityArray.contains(Integer.toString(currentPriority))){
				context.setStopped(true);
				return true;
			}
		}
	
		return false;
	}

	@Override
	public void setTestMethods(List<ITestNGMethod> testMethods) {
		// TODO Auto-generated method stub

	}

	private static String stringArrayToString(final String[] aStringArray) {
		final StringBuilder msg = new StringBuilder("[");
		if (aStringArray != null) {
			for (int i = 0; i < aStringArray.length; i++) {
				msg.append(aStringArray[i]);
				if (i < (aStringArray.length - 1)) {
					msg.append(", ");
				}
			}
		}
		msg.append("]");
		return msg.toString();
	}

}