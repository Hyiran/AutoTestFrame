package com.atf.cap.internal.suite;

import org.apache.commons.lang3.StringUtils;

public class UIJobSetUpListener extends JobSetUpListener {
    @Override
    public void initParams() {
        super.initParams();
          
        String brower = System.getenv("browserType");
        logger.info("browserType {} in Environment ", brower);
        brower = StringUtils.defaultString(brower, "Chrome");
        
        jobInfo.setBrowerName(brower);  
        jobInfo.setRunType("UI");
    }

}
