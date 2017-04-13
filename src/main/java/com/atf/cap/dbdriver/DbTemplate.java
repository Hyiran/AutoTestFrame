package com.atf.cap.dbdriver;

import java.util.List;
import java.util.Map;

/**
 * @author liuxb
 */
public interface DbTemplate {

    int doExecute(String sql);

    List<Map<String, Object>> queryResultSet(String sql);

    String querySingleString(String sql);

    Integer querySingleInteger(String sql);

    Double querySingleDouble(String sql);


}
