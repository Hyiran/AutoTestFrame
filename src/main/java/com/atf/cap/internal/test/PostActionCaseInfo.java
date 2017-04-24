package com.atf.cap.internal.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;

import com.atf.cap.at.client.AvatarServiceClient;
import com.atf.cap.framework.domain.LogCase;
import com.atf.cap.internal.data.DataMoude.DataServiceHolder;
import com.atf.cap.internal.domain.Context;
import com.atf.cap.internal.suite.JobSetUpListener;

public class PostActionCaseInfo implements MethodFilter {

	private static final Logger logger = LoggerFactory
			.getLogger(PostActionCaseInfo.class);

	@Override
	public void doFilter(Context context) {
		try {
			AvatarServiceClient client = DataServiceHolder
					.avatarServiceClient();

			ITestResult iTestResult = context.getTestResult();
			LogCase logCase = new LogCase();
			logCase.setCaseID(context.getCaseInfo().getCaseId().intValue());
			logCase.setResult(iTestResult.getStatus());

			client.writeBackCaseResult(logCase);
			// real time compute job run result

			client.writeBackRunResult(JobSetUpListener.jobInfo().getJobID()
					.intValue());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

}
