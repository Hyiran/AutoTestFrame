package com.atf.cap.internal.performance;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.ProfilerRegistry;

/**
 * 
 * @author sqou
 *
 */
public class Profilers {

	private static final Logger logger = LoggerFactory.getLogger(Profilers.class);

	private static final Map<String, Profiler> profilers = new HashMap<String, Profiler>();

	private static final String appium = "preAppium";

	private static final AtomicInteger listenerOrder = new AtomicInteger();

	public static synchronized Profiler getProfiler(String key) {

		if (profilers.containsKey(key)) {
			return profilers.get(key);
		}

		Profiler p = new Profiler(key);
		ProfilerRegistry profilerRegistry = ProfilerRegistry
				.getThreadContextInstance();
		p.registerWith(profilerRegistry);
		p.setLogger(logger);

		profilers.put(key, p);
		return p;

	}

	public static Profiler preAppium() {
		return getProfiler(appium);
	}

	public static Integer listenerOrder() {
		return listenerOrder.incrementAndGet();
	}
}
