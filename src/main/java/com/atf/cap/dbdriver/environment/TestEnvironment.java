package com.atf.cap.dbdriver.environment;

/**
 * Provide all possible DB environment definitions.
 *
 * @author liuxb
 *         2015/9/25
 */
public enum TestEnvironment {

    DEV(0, "dev"),

    FAT(1, "fat"),

    FWS(2, "fws"),

    UAT(3, "uat"),

    BAOLEI(4, "baolei"),

    LPT(5, "lpt"),

    PROD(6, "prod");

    private int envId;

    private String envName;

    public int getEnvId() {
        return envId;
    }

    public String getEnvName() {
        return envName;
    }

    TestEnvironment(Integer envId, String envName) {
        this.envId = envId;
        this.envName = envName;
    }

    public TestEnvironment getEnvironment(String name) {
        for (TestEnvironment environment : TestEnvironment.values()) {
            if (environment.getEnvName().equalsIgnoreCase(name)) {
                return environment;
            }
        }
        // input matches none of existing definition, use local as failover
        return DEV;
    }
}
