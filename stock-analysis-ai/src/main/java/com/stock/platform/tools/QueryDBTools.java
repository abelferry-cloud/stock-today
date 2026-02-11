package com.stock.platform.tools;

import org.springframework.ai.tool.annotation.Tool;

public class QueryDBTools {

    @Tool(description = "查询个股数据库")
    public String queryDB(String sql) {
        return null;
    }
}
