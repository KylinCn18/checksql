package com.kylincn.financecore.bean;

/**
 * description: CheckSqlResult <br>
 * date: 2019/12/16 16:05 <br>
 * author: 18042621 <br>
 * version: 1.0 <br>
 */
public class CheckSqlResult {

    private String fileName;

    private String sqlId ;

    private String sql ;

    private String index;

    private String tips;

    public CheckSqlResult() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getSqlId() {
        return sqlId;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }
}
