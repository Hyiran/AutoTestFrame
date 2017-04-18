package com.atf.cap.internal.data;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atf.cap.at.client.AvatarServiceClient;
import com.atf.cap.at.client.RunAtServiceClient;
import com.atf.cap.at.client.mock.MockAvatarServiceClient;
import com.atf.cap.at.client.mock.MockRunAtServiceClient;

/**
 * 即使在Lab中运行，数据服务崩溃也不会阻止Cap的运行
 * 
 * @author sqou
 *
 * @param <T>
 */
public class SilenceTimeoutClientProxy implements InvocationHandler {

	private static final ExecutorService dataExecutorService = Executors
			.newSingleThreadScheduledExecutor();

	private static final AtomicBoolean dirty = new AtomicBoolean(false);

	private static final Logger logger = LoggerFactory
			.getLogger(SilenceTimeoutClientProxy.class);

	static final AvatarServiceClient avatar = new MockAvatarServiceClient();
	static final RunAtServiceClient runat = new MockRunAtServiceClient();

	private Object target;

	public SilenceTimeoutClientProxy(Object target) {
		super();
		this.target = target;
	}

	/**
	 * Lab run on real data client,but will be downgrade to mock client if any
	 * error thrown
	 */
	@Override
	public Object invoke(Object proxy, final Method method, final Object[] args)
			throws Throwable {

		Object result = null;
		if (dirty.get()) {
			return callMock(method, args);
		}

		try {
			Future<Object> future = dataExecutorService
					.submit(new Callable<Object>() {

						@Override
						public Object call() throws Exception {
							try {
								return method.invoke(target, args);
							} catch (Exception e) {
								throw e;
							}
						}
					});
			result = future.get(10, TimeUnit.SECONDS);
		} catch (Exception e) {

			dirty.set(true);
			logger.warn(
					"maybe the bad network or we should restart data server", e);

			result = callMock(method, args);
		}
		return result;
	}

	private Object callMock(final Method method, final Object[] args)

	{
		Object result = null;
		try {
			if (target instanceof AvatarServiceClient) {
				Method t = avatar.getClass().getMethod(method.getName(),
						method.getParameterTypes());
				result = t.invoke(avatar, args);

			} else {
				Method t = runat.getClass().getMethod(method.getName(),
						method.getParameterTypes());
				result = t.invoke(runat, args);
			}
		} catch (Exception e) {
			logger.warn("Data Service error,change to mock client", e);
		}
		return result;
	}

	public static Object newInstance(Object target) {

		return Proxy.newProxyInstance(target.getClass().getClassLoader(),
				target.getClass().getInterfaces(),
				new SilenceTimeoutClientProxy(target));

	}

}
