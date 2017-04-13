package com.atf.cap.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer2;
import org.testng.annotations.IConfigurationAnnotation;
import org.testng.annotations.IDataProviderAnnotation;
import org.testng.annotations.IFactoryAnnotation;
import org.testng.annotations.ITestAnnotation;

/**
 * To be Implemented for a better user experience in near future
 * 
 * @author sqou
 *
 */

@SuppressWarnings("rawtypes")
public class TestNGAnnotationTransformer implements IAnnotationTransformer2 {

	@Override
	public void transform(ITestAnnotation annotation, Class testClass,
			Constructor testConstructor, Method testMethod) {

	}

	@Override
	public void transform(IConfigurationAnnotation annotation, Class testClass,
			Constructor testConstructor, Method testMethod) {

	}

	@Override
	public void transform(IDataProviderAnnotation annotation, Method method) {

	}

	@Override
	public void transform(IFactoryAnnotation annotation, Method method) {

	}

}
