package com.atf.cap.internal.suite;

/**
 * Created by sqou on 2015/4/21.
 */
public class APIJobSetUpListener extends JobSetUpListener {
    @Override
    public void initParams() {
        super.initParams();
        jobInfo.setBrowerName(null);
        jobInfo.setRunType("API");
    }

}
