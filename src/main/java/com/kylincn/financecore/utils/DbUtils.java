package com.suning.financecore.utils;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import com.alibaba.druid.sql.dialect.db2.parser.DB2StatementParser;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.suning.financecore.bean.DataSourceInfo;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * description: DbUtisl <br>
 * date: 2019/12/11 16:22 <br>
 * author: 18042621 <br>
 * version: 1.0 <br>
 */
public class DbUtils {

    private static final Map<String, Connection> connectionsMap = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(DbUtils.class);

    /**
     * 初始化数据库连接
     *
     * @param dataSourceInfo
     * @throws Exception
     */
    private static Connection getConnection(DataSourceInfo dataSourceInfo) throws Exception {
        Connection connection = connectionsMap.get(dataSourceInfo.getGroupName());
        if (connection == null) {
            //数据库驱动类型
            String driverClass = CheckSqlConstant.SQLDRIVERCLASSMAP.get(dataSourceInfo.getDbType());
            //数据库连接
            String url = dataSourceInfo.getDbUrl();
            //数据库用户名
            String userName = dataSourceInfo.getDbUserName();
            //数据库密码
            String password = dataSourceInfo.getDbPassword();
            Class.forName(driverClass);
            connection = DriverManager.getConnection(url, userName, password);
            connectionsMap.put(dataSourceInfo.getGroupName(), connection);
        }
        return connection;
    }


    public static List<Map<String, Object>> getQueryDataList(DataSourceInfo dataSourceInfo, String sql, List<Object> params) {
        //返回值
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            Connection connection = getConnection(dataSourceInfo);
            PreparedStatement statement = connection.prepareStatement(sql);
            if (params != null && !params.isEmpty()) {
                for (int i = 0; i < params.size(); i++) {
                    statement.setObject(i + 1, params.get(i));
                }
            }
            //查询
            ResultSet resultSet = statement.executeQuery();
            if (resultSet != null) {
                ResultSetMetaData metaData;
                Map<String, Object> tempMap;
                while (resultSet.next()) {
                    //获取列集
                    metaData = resultSet.getMetaData();
                    tempMap = new HashMap<>();
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        String columnName = metaData.getColumnName(i);
                        Object value = resultSet.getObject(columnName);
                        tempMap.put(columnName, value);
                    }
                    resultList.add(tempMap);
                }
            }
        } catch (Exception e) {
            LOGGER.error("checksql连接数据库异常：{}", e.getStackTrace());
            return null;
        }
        return resultList;
    }


    public static Map<String, Object> queryTableConstruct(DataSourceInfo dataSourceInfo, String sql) {

        Map<String, Object> tableConstructMap = new CaseInsensitiveMap();
        try {
            Connection connection = getConnection(dataSourceInfo);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet != null) {
                ResultSetMetaData metaData;
                String columnName = "";
                String columnType = "";
                while (resultSet.next()) {
                    metaData = resultSet.getMetaData();
                    for (int i = 1; i <= 2; i++) {
                        String columnTempName = metaData.getColumnName(i);
                        if (i == 1) {
                            columnName = resultSet.getString(columnTempName).trim();
                        } else {
                            columnType = resultSet.getString(columnTempName).trim();
                        }
                    }
                    tableConstructMap.put(columnName, columnType);
                }
            }
        } catch (Exception e) {
            LOGGER.error("checksql 查询表结构信息异常：{}", e.getStackTrace());
        }
        return tableConstructMap;
    }

    public static void close() {
        if (!connectionsMap.isEmpty()) {
            for (Map.Entry<String, Connection> entry : connectionsMap.entrySet()) {
                try {
                    entry.getValue().close();
                } catch (SQLException e) {
                    LOGGER.error("关闭sqlSession异常：{}", e);
                }
            }
        }
    }

    public static void main(String[] args) {
        String t = "SELECT BUSINESSTEMPLATEID AS TEMPLATEID,BUSINESSTEMPLATENAME AS TEMPLATENAME \t\t\t\tFROM  WORKFLOWTEMPLATE \t\t\t\tORDER BY  CAST(SUBSTR(BUSINESSTEMPLATEID,5) AS BIGINT) ASC \t\t\t\tWITH UR";
        /*Pattern compile = Pattern.compile("FROM(?!\\s*\\()(.*)WHERE|ORDER|GROUP|\\(");
        Matcher matcher = compile.matcher(t);
        if (matcher.find()) {
            System.out.println(matcher.group(1));
            String group = matcher.group(1);
            String[] split = group.split(",");
            for (int i = 0; i < split.length; i++) {
                t = t.replaceAll(split[i], " 哈哈哈." + split[i]);
            }
            System.out.println(t);
        }*/

    }

}
