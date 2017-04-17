package com.atf.cap.dbdriver.environment;

/**
 * @author liuxb
 *         2015/9/25
 */
public enum DatabaseDriverName {

    H2(1, "h2", "org.h2.Driver", 9092),

    HSQLDB(2, "hssql", "org.hsqldb.jdbc.JDBCDriver", 9001),

    MySql(3, "mysql", " com.mysql.jdbc.Driver", 3306),

    SQLServer(4, "sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver", 1433);

    private int driverId;

    private String driverType;

    private String driverName;

    private int driverPort;

    public int getDriverId() {
        return driverId;
    }

    public String getDriverType() {
        return driverType;
    }

    public String getDriverName() {
        return driverName;
    }

    public int getDriverPort() {
        return driverPort;
    }

    DatabaseDriverName(int driverId, String driverType, String driverName, int driverPort) {
        this.driverId = driverId;
        this.driverType = driverType;
        this.driverName = driverName;
        this.driverPort = driverPort;
    }

    public DatabaseDriverName getDbDriverName(String name) {
        for (DatabaseDriverName driver : DatabaseDriverName.values()) {
            if (driver.getDriverType().equalsIgnoreCase(name)) {
                return driver;
            }
        }
        return null;
    }
}
