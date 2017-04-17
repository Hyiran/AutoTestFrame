package com.atf.cap.dbdriver;

import com.atf.cap.exception.CapDbAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.InputStream;

/**
 * @author oushiqiang
 */
public class DbTemplateFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbTemplateFactory.class);

    private DataSource dataSource;

    /**
     * Get DbTemplate from plain text configurations.
     *
     * @param dbDriver
     * @param dbUrl
     * @param userName
     * @param password
     * @return
     */
    public static DbTemplate getTemplateFromPlainTxt(String dbDriver, String dbUrl, String userName, String password) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(dbDriver);
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        return new DbPlainTemplate(jdbcTemplate);
    }

    /**
     * Get DbTemplate from the default provided properties.
     *
     * @return
     */
    public static DbTemplate getTemplateFromProperties() {

        LOGGER.trace("In BeforeClass method ------ Prepare database connection");

        DbTemplate dbTemplate = null;

        // try to check if the db-connection file exists
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream(CapDBConstants.DB_CONNECTION_PROPERTIES);

        if (stream != null) {
            LOGGER.trace("Found cap-db-conn.properties, Use in-container db connection --------");
            JdbcConnectInfo connectInfo = DbInitializer.initDataSource(stream);

            if (connectInfo != null) {
                dbTemplate = getTemplateFromPlainTxt(connectInfo.getDbDriver(), connectInfo.getDbUrl(),
                        connectInfo.getUsername(), connectInfo.getPassword());
            }
        }

        if (dbTemplate != null) {
            LOGGER.trace("Initialized DbTemplate from cap-db-conn.properties successfully.");
            return dbTemplate;
        }

        throw new CapDbAccessException("Not able to initialize DbTemplate from cap-db-conn.properties.");
    }

    /**
     * Not really used.
     *
     * @param dataSource
     * @return
     */
    public static DbTemplate getTemplateFromSpringContext(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return new DbPlainTemplate(jdbcTemplate);

    }
    
    public static void main(String args[])
    {
    	DbTemplate templeate=DbTemplateFactory
    			.getTemplateFromPlainTxt("com.mysql.jdbc.Driver",
    						"jdbc:mysql://127.0.0.1:3306/atp?useUnicode=true&characterEncoding=utf-8",
    						"root",
    						"123456");
    	System.out.print(templeate.queryResultSet("SELECT * FROM user_info"));
    }


}
