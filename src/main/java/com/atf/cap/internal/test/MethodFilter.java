package com.atf.cap.internal.test;

import com.atf.cap.internal.domain.Context;

/**
 * this filter should be thread safe in method level context
 * 
 * @author sqou
 *
 */
public interface MethodFilter {

	public void doFilter(Context context);
}
