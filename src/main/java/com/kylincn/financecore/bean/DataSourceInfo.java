package com.kylincn.financecore.bean;

/**
 * description: DataSourceInfo <br>
 * date: 2019/12/20 16:40 <br>
 * author: 18042621 <br>
 * version: 1.0 <br>
 */
public class DataSourceInfo {

    private String groupName;

    private String serverIp;

    private String serverUserName;

    private String serverPassword;

    private String dbUserName;

    private String dbPassword;

    private String dbType;

    private String dataBaseName;

    private String mapperNames;

    private String scheme;

    private String dbPort;

    public String getDbPort() {
        return dbPort;
    }

    public void setDbPort(String dbPort) {
        this.dbPort = dbPort;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getServerUserName() {
        return serverUserName;
    }

    public void setServerUserName(String serverUserName) {
        this.serverUserName = serverUserName;
    }

    public String getServerPassword() {
        return serverPassword;
    }

    public void setServerPassword(String serverPassword) {
        this.serverPassword = serverPassword;
    }

    public String getDbUserName() {
        return dbUserName;
    }

    public void setDbUserName(String dbUserName) {
        this.dbUserName = dbUserName;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public String getMapperNames() {
        return mapperNames;
    }

    public void setMapperNames(String mapperNames) {
        this.mapperNames = mapperNames;
    }

    public String getDbUrl() {
        StringBuilder stringBuilder = new StringBuilder();
        if (dbType.equals("db2")) {
            stringBuilder.append("jdbc:db2://");
        } else if (dbType.equals("mysql")) {
            stringBuilder.append("jdbc:mysql://");
        }
        stringBuilder.append(serverIp).append(":").append(dbPort).
                append("/").append(dataBaseName).append(":").append("currentSchema=").append(scheme).append(";");
        return stringBuilder.toString();
    }
}
