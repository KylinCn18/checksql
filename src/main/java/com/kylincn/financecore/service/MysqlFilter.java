package com.suning.financecore.service;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * description: MysqlFilter <br>
 * date: 2019/12/23 16:27 <br>
 * author: 18042621 <br>
 * version: 1.0 <br>
 */
public class MysqlFilter extends FilterEventAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(MysqlFilter.class);

    private static final String TABLE_FIELD_TENANT_ID = "tenant_id";

    private static final String MYSQL_STRING = "mysql";

    private static final List<String> NOT_HAVE_TENANT_ID_TABLE_LIST = Arrays.asList("bi_bank",
            "bi_support_charge_detail");



    /**
     * 处理delete语句
     *
     * @param sql  sql语句
     * @param stmt 解析的语句
     * @return 修改的后的sql
     */
    private String doDeleteSql(String sql, SQLStatement stmt) {
        MySqlDeleteStatement delete = (MySqlDeleteStatement) stmt;
        SQLExpr where = delete.getWhere();
        delete.setTableName("哈昂去昂");
        return delete.toString();
    }

    public static void main(String[] args) {

    }

}
