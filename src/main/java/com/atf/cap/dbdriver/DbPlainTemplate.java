package com.atf.cap.dbdriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static com.atf.cap.Assertion.fail;

/**
 * @author liuxb
 *
 */
public class DbPlainTemplate implements DbTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbPlainTemplate.class);


    private JdbcTemplate jdbcTemplate;

    public DbPlainTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void executePlainSql(String sql) {
        this.jdbcTemplate.execute(sql);
    }

    /**
     * Execute given sql sentences. Return 0 if executes successfully, otherwise -1.
     * @param sql
     * @return
     */
    @Override
    public int doExecute(String sql) {
        LOGGER.trace("The executing sql is: " + sql);

        try {
            jdbcTemplate.execute(sql);
            return 0;
        } catch (Exception e) {
            LOGGER.error("Error happens when executing sql: {}", sql, e);
            return -1;
        }
    }

    private <T> T querySingleValue(String sql, Class<T> type) {
        LOGGER.trace("The executing sql is: " + sql);
        T result =  jdbcTemplate.queryForObject(sql, type);
        LOGGER.trace("The result is: " + result);
        return result;
    }

    @Override
    public String querySingleString(String sql) {
        return querySingleValue(sql, String.class);
    }

    @Override
    public Integer querySingleInteger(String sql) {
        return querySingleValue(sql, Integer.class);
    }

    @Override
    public Double querySingleDouble(String sql) {
        return querySingleValue(sql, Double.class);
    }

    @Override
    public List<Map<String, Object>> queryResultSet(String sql) {
        return jdbcTemplate.queryForList(sql);
    }

}
