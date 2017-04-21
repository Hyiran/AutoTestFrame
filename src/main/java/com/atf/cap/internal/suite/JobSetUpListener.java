package com.atf.cap.internal.suite;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IExecutionListener;

import com.atf.cap.at.client.AvatarServiceClient;
import com.atf.cap.at.client.RunAtServiceClient;
import com.atf.cap.framework.domain.LogJob;
import com.atf.cap.framework.domain.LogRunDetail;
import com.atf.cap.internal.data.DataMoude.DataServiceHolder;
import com.atf.cap.internal.domain.JobInfo;
import com.atf.cap.internal.performance.Profilers;
import com.atf.cap.internal.util.NetUtils;
import com.atf.cap.lanucher.Environment;

public class JobSetUpListener implements IExecutionListener {

    Logger logger = LoggerFactory.getLogger(JobSetUpListener.class);

    protected static final JobInfo jobInfo = new JobInfo();

    RunAtServiceClient client = DataServiceHolder.runAtServiceClient();
    AvatarServiceClient avatarClient = DataServiceHolder.avatarServiceClient();

    @Override
    public void onExecutionStart() {
        logger.info("onExecutionStart");
        Profilers.preAppium().start("initParams");

        initParams();

        Profilers.preAppium().start("initClient");
        initClient();

        Profilers.preAppium().start("setUpJobInfo");
        setUpRunAt();

        logger.info("Global JobInfo: {}", jobInfo);

    }

    public void initClient() {

        logger.info("initClient");

        logger.info("set up data module");
        if (Environment.isLab()) { // to be refactored
            String dataURI = Environment.getDataURI();

            logger.info("load data module on {}", dataURI);
            DataServiceHolder.setUp(dataURI);
        }

    }

    public void setUpRunAt() {

        logger.info("set up job info");
        try {

            LogJob logJob = new LogJob();
            logJob.setGmtCreate(new Date());
            logJob.setEnv(jobInfo.getEnv());
            logJob.setBrowser(jobInfo.getBrowerName());
            logJob.setStatus(1);
            logJob.setJobName(jobInfo.getJobName());
            logJob.setSlaveIP(jobInfo.getHostIp());
            logJob.setSlaveName(jobInfo.getHostName());
            logJob.setProjectName(jobInfo.getProjectName());
            logJob.setPath(jobInfo.getPath());
            logJob.setRunType(jobInfo.getRunType());

            logger.debug("logjob parameter {}", logJob);
            int jobId = client.createJobID(logJob);

            jobInfo.setJobID(Long.valueOf(jobId));
            LogRunDetail logRunDetail = new LogRunDetail(jobInfo.getRunID()
                    .intValue(), jobInfo.getJobName(), jobInfo.getJobID()
                    .intValue(), -1, new Date());
            client.addCILogRunDetail(logRunDetail);

            logJob = new LogJob();
            logJob.setJobID(jobId);
            logJob.setRunID(jobInfo.getRunID().intValue());

            client.addCiLogJob(logJob);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    public void initParams() {
    	Map<String, String> paramMap = Environment.getParamMap();
    	String runId =null;
    	String env=null;
    	
    	if(paramMap.size() > 0){
    		runId = paramMap.get("runid");
    		env = paramMap.get("env");
    	}
    	else{
    		runId = System.getenv("runid");
    		env = System.getenv("env");
    	}
    			
        logger.info("runID {} in Environment", runId);
        Long runIdL = runId == null ? 0 : Long.valueOf(runId);
       
        logger.info("env {} in Environment ", env);
        env = StringUtils.defaultString(env, "uat");

        jobInfo.setRunID(runIdL);
        jobInfo.setEnv(env);
        jobInfo.setBrowerName("Chrome");
        jobInfo.setRunType("APP");

        String projectName = new File(System.getProperty("user.dir")).getName();
        jobInfo.setProjectName(projectName);
        jobInfo.setJobName(System.getenv("JOB_NAME"));
        jobInfo.setHostIp(NetUtils.getLocalIp());
        jobInfo.setHostName(NetUtils.getLocalName());

        jobInfo.setPath(System.getProperty("user.dir"));

    }


    @Override
    public void onExecutionFinish() {
        try {
            avatarClient.writeBackRunResult(jobInfo.getJobID().intValue());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

        } finally {
            logger.info("job completed");
        }
    }

    public static JobInfo jobInfo() {
        return jobInfo;
    }

}
