package com.kylincn.financecore.utils;

import org.apache.commons.collections.map.CaseInsensitiveMap;

import java.util.HashMap;
import java.util.Map;

/**
 * description: CheckSqlConstant <br>
 * date: 2019/12/13 10:59 <br>
 * author: 18042621 <br>
 * version: 1.0 <br>
 */
public class CheckSqlConstant {

    public static final Map<String, Object> SQLDEFUALVALUE = new CaseInsensitiveMap();

    public static final Map<String, String> SQLDRIVERCLASSMAP = new CaseInsensitiveMap();

    static {
        SQLDEFUALVALUE.put("tinyint", 1);
        SQLDEFUALVALUE.put("smallint", 1);
        SQLDEFUALVALUE.put("mediumint", 1);
        SQLDEFUALVALUE.put("int", 1);
        SQLDEFUALVALUE.put("integer", 1);
        SQLDEFUALVALUE.put("bigint", 1);
        SQLDEFUALVALUE.put("float", 1.0);
        SQLDEFUALVALUE.put("double", 1.0);
        SQLDEFUALVALUE.put("decimal", 1.0);
        SQLDEFUALVALUE.put("date", "'2019-12-12'");
        SQLDEFUALVALUE.put("time", "'12:12:12'");
        SQLDEFUALVALUE.put("year", "'2019'");
        SQLDEFUALVALUE.put("datetime", "'2019-12-12 12:12:12'");
        SQLDEFUALVALUE.put("timestamp", "'20191212 12:12:12'");
        SQLDEFUALVALUE.put("timestmp", "'20191212 12:12:12'");
        SQLDEFUALVALUE.put("char", "'123'");
        SQLDEFUALVALUE.put("varchar", "'123'");
        SQLDEFUALVALUE.put("tinyblob", "'123'");
        SQLDEFUALVALUE.put("tinytext", "'123'");
        SQLDEFUALVALUE.put("blob", "'123'");
        SQLDEFUALVALUE.put("text", "'123'");
        SQLDEFUALVALUE.put("mediumblob", "'123'");
        SQLDEFUALVALUE.put("mediumtext", "'123'");
        SQLDEFUALVALUE.put("longblob", "'123'");
        SQLDEFUALVALUE.put("longtext", "'123'");
        SQLDEFUALVALUE.put("bit", 1);

        SQLDRIVERCLASSMAP.put("mysql","com.mysql.jdbc.Driver");
        SQLDRIVERCLASSMAP.put("db2","COM.ibm.db2os390.sqlj.jdbc.DB2SQLJDriver");


    }

    /**
     * 邮件配置
     */
    public static final String MAIL_SERVER = "smtp.suning.com";

    public static final String MAIL_SENDER = "FAFLOW@suning.com";

    public static final String MAIL_NICKNAME = "金融账务核心产品研发中心";

    public static final String MAIL_USER = "FAFLOW@suning.com";

    public static final String MAIL_PASSWORD = "PASSWORD";
}
