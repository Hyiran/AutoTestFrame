package com.atf.cap.dbdriver;

/**
 * @author liuxb
 *         2015/9/25
 */
public class JdbcConnectInfo {

    public static final String KEY_USERNAME = "uid";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_SERVER = "server";
    public static final String KEY_PORT = "port";
    public static final String KEY_INSTANCE = "database";

    private String dbDriver;
    private String dbUrl;
    private String username;
    private String password;
    private String dbServer;
    private int dbPort;
    private String dbInstance;


    public JdbcConnectInfo() {

    }

    public JdbcConnectInfo(String dbDriver, String dbUrl, String username, String password) {
        this.dbDriver = dbDriver;
        this.dbUrl = dbUrl;
        this.username = username;
        this.password = password;
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public void setDbDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbServer() {
        return dbServer;
    }

    public void setDbServer(String dbServer) {
        this.dbServer = dbServer;
    }

    public int getDbPort() {
        return dbPort;
    }

    public void setDbPort(int dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbInstance() {
        return dbInstance;
    }

    public void setDbInstance(String dbInstance) {
        this.dbInstance = dbInstance;
    }

    @Override
    public String toString() {
        return "JdbcConnectInfo{" +
                "dbDriver='" + dbDriver + '\'' +
                ", dbUrl='" + dbUrl + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", dbServer='" + dbServer + '\'' +
                ", dbPort=" + dbPort +
                ", dbInstance='" + dbInstance + '\'' +
                '}';
    }
}
