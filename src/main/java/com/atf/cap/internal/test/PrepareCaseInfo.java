package com.atf.cap.internal.test;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atf.cap.at.client.AvatarServiceClient;
import com.atf.cap.framework.domain.LogCase;
import com.atf.cap.framework.domain.LogCaseInfo;
import com.atf.cap.framework.domain.TestBaseDomain;
import com.atf.cap.framework.domain.dto.CaseDTO;
import com.atf.cap.internal.data.DataMoude.DataServiceHolder;
import com.atf.cap.internal.domain.CaseInfo;
import com.atf.cap.internal.domain.Context;
import com.atf.cap.internal.domain.JobInfo;
import com.atf.cap.internal.suite.JobSetUpListener;

/**
 * 
 *
 */
public class PrepareCaseInfo implements MethodFilter {
	private static final Logger logger = LoggerFactory
			.getLogger(PrepareCaseInfo.class);

	@Override
	public void doFilter(Context context) {

		CaseInfo caseInfo = new CaseInfo();
		JobInfo jobInfo = JobSetUpListener.jobInfo();

		caseInfo.setJobId(jobInfo.getJobID());
		caseInfo.setProjectName(jobInfo.getProjectName());

		String methodName = context.getMethod().getName();
		String className = context.getMethod().getDeclaringClass().getName();

		caseInfo.setMethodName(methodName);
		caseInfo.setClassName(className);

		context.setCaseInfo(caseInfo);

		try {
			// setup too duplicated!
			LogCase logCase = new LogCase(caseInfo.getJobId().intValue(), 0,
					new Date(), caseInfo.getOwner(), caseInfo.getEmail(),
					caseInfo.getTitle(), caseInfo.getCategory(),
					caseInfo.getPriority());
			LogCaseInfo logCaseInfo = new LogCaseInfo(
					caseInfo.getProjectName(), caseInfo.getClassName(),
					caseInfo.getMethodName());

			TestBaseDomain testBaseDomain = new TestBaseDomain(
					caseInfo.getClassName(), caseInfo.getMethodName(),
					caseInfo.getOwner(), caseInfo.getTitle(),
					caseInfo.getCategory(), caseInfo.getPriority());

			CaseDTO caseDTO = new CaseDTO(logCase, logCaseInfo, testBaseDomain);

			AvatarServiceClient client = DataServiceHolder
					.avatarServiceClient();
			LogCase logCase2 = client.createCase(caseDTO);
			caseInfo.setCaseId(Long.valueOf(logCase2.getCaseID()));
			caseInfo.setCaseInfoId(Long.valueOf(logCase2.getCaseInfoID()));

		} catch (Exception e) {
			logger.warn("create caseinfo ", e);
			// throw new CapException("set up case info", e);

		}

	}
}
