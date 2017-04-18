package com.atf.cap.internal.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atf.cap.lanucher.Environment;
import com.atf.cap.at.client.AvatarServiceClient;
import com.atf.cap.at.client.RunAtServiceClient;
import com.atf.cap.at.client.impl.CapAvatarDataServiceClientImp;
import com.atf.cap.at.client.impl.CapRunAtDataServiceClientImp;
import com.atf.cap.at.client.mock.MockAvatarServiceClient;
import com.atf.cap.at.client.mock.MockRunAtServiceClient;
import com.atf.cap.client.property.CapClientProperties;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 写入Portal数据的Data模块管理
 * 
 * to be rafactored
 * 
 * @author sqou
 */
public class DataMoude extends AbstractModule {

	private static final Logger logger = LoggerFactory
			.getLogger(DataMoude.class);
	private Injector injector;

	private volatile boolean initialized;

	private DataMoude() {
		super();
	}

	@Override
	protected void configure() {

		if (Environment.isLab()) {
			AvatarServiceClient avatarClient = new CapAvatarDataServiceClientImp();
			RunAtServiceClient runatClient = new CapRunAtDataServiceClientImp();

			AvatarServiceClient avatarProxy = (AvatarServiceClient) SilenceTimeoutClientProxy
					.newInstance(avatarClient);

			RunAtServiceClient runAtProxy = (RunAtServiceClient) SilenceTimeoutClientProxy
					.newInstance(runatClient);

			bind(AvatarServiceClient.class).toInstance(avatarProxy);

			bind(RunAtServiceClient.class).toInstance(runAtProxy);
		} else {
			bind(AvatarServiceClient.class).to(MockAvatarServiceClient.class);
			bind(RunAtServiceClient.class).to(MockRunAtServiceClient.class);
		}

	}

	public AvatarServiceClient avatarServiceClient() {
		return injector.getInstance(AvatarServiceClient.class);
	}

	public RunAtServiceClient runAtServiceClient() {
		return injector.getInstance(RunAtServiceClient.class);
	}

	public void config(String dataUrl) {

		if (initialized) {
			logger.error("Module has been initialized, nothing happen");
			return;
		}
		CapClientProperties.capDataUrl = Environment.getDataURI();
		initialized = true;
	}

	public void construct() {
		injector = Guice.createInjector(new DataMoude());
	}

	public static class DataServiceHolder {

		private static final DataMoude dataMoude;;
		static {
			dataMoude = new DataMoude();
			dataMoude.construct();
		}

		public static void setUp(String dataUrl) {
			dataMoude.config(dataUrl);
		}

		public static AvatarServiceClient avatarServiceClient() {
			return dataMoude.avatarServiceClient();
		}

		public static RunAtServiceClient runAtServiceClient() {
			return dataMoude.runAtServiceClient();
		}
	}

}
