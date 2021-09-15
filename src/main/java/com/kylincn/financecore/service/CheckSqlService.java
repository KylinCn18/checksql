package com.kylincn.financecore.service;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.db2.parser.DB2StatementParser;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.StringUtils;
import com.kylincn.financecore.bean.CheckSqlResult;
import com.kylincn.financecore.bean.DataSourceInfo;
import com.kylincn.financecore.utils.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * description: CheckSqlService <br>
 * date: 2019/12/11 15:40 <br>
 * author: 18042621 <br>
 * version: 1.0 <br>
 */
@Mojo(name = "checksql", defaultPhase = LifecyclePhase.PACKAGE)
public class CheckSqlService extends AbstractMojo {

    private static final String DEFAULT_TEMPLATE_KEY = "default_template_key";
    private static final Configuration CONFIGURER = new Configuration();

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckSqlService.class);

    /**
     * 配置文件的地址
     */
    @Parameter(property = "mapperPath", required = true)
    private String mapperPath;

    /**
     * 收件人 邮件地址
     */
    @Parameter(property = "email", required = true)
    private String email;

    /**
     * 系统名
     */
    @Parameter(property = "sysName", required = true)
    private String sysName;

    /**
     * 数据源组
     */
    @Parameter(property = "dataSourceInfos", required = true)
    private List<DataSourceInfo> dataSourceInfos;

    public static void main(String[] args) throws MojoExecutionException {
        CheckSqlService checkSqlService = new CheckSqlService();
        checkSqlService.mapperPath = "G:\\ideaWorkSpace\\checksql\\src\\main\\resources\\mapper";
        checkSqlService.email = "18042621@suning.com";
        checkSqlService.sysName = "faflow";

        List<DataSourceInfo> dataSourceInfos = new ArrayList<>();
        DataSourceInfo dataSourceInfo = new DataSourceInfo();
        dataSourceInfo.setGroupName("test1");
        dataSourceInfo.setServerIp("10.27.95.179");
        dataSourceInfo.setServerUserName("scsinst");
        dataSourceInfo.setServerPassword("jqV1KtKpdvPP");
        dataSourceInfo.setDbType("db2");
        dataSourceInfo.setDbUserName("scsuser");
        dataSourceInfo.setDbPassword("%3*734C5^");
        dataSourceInfo.setDataBaseName("scsdb");
        dataSourceInfo.setScheme("SCSUSER");
        dataSourceInfo.setMapperNames("test.xml");
        dataSourceInfo.setDbPort("60004");

        /*dataSourceInfo.setGroupName("faaccDataSource");
        dataSourceInfo.setServerIp("10.27.70.133");
        dataSourceInfo.setServerUserName("facins");
        dataSourceInfo.setServerPassword("cYeB7nL2OTuE");
        dataSourceInfo.setDbType("db2");
        dataSourceInfo.setDbUserName("yunwei");
        dataSourceInfo.setDbPassword("aiPhaika6Eet");
        dataSourceInfo.setDataBaseName("FAACCDB");
        dataSourceInfo.setScheme("FACDBA");
        dataSourceInfo.setMapperNames("test.xml");
        dataSourceInfo.setDbPort("60004");*/
        dataSourceInfos.add(dataSourceInfo);
        checkSqlService.dataSourceInfos = dataSourceInfos;

        checkSqlService.execute();
    }

    @Override
    public void execute() throws MojoExecutionException {
        //获取路径
        File file = new File(mapperPath);
        List<CheckSqlResult> checkSqlResults = new ArrayList<>();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().contains("xml")) {
                    List<CheckSqlResult> tempList = excuteExplain(files[i], getDataSourceInfo(files[i].getName()));
                    checkSqlResults.addAll(tempList);
                } else {
                    continue;
                }
            }
        } else if (file.getName().contains("xml")) {
            List<CheckSqlResult> tempList = excuteExplain(file, getDataSourceInfo(file.getName()));
            checkSqlResults.addAll(tempList);
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("dataList", checkSqlResults);
        paramMap.put("sysName", sysName);
        try {
            MailUtilTemplate.sendMailByTemplate(email, "关于【" + sysName + "】系统的SQL检测结果", paramMap, "checksqlresulttemplate.ftl");
        } catch (Exception e) {
            LOGGER.error("checksql 发送邮件已异常：{}", e);
        } finally {
            DbUtils.close();
            LinuxUtil.closeSession();
        }
    }

    /**
     * 把SQL转成 具体的sql
     *
     * @param file           mapper文件
     * @param dataSourceInfo 数据库配置信息
     * @return
     */
    private List<CheckSqlResult> excuteExplain(File file, DataSourceInfo dataSourceInfo) {

        List<CheckSqlResult> results = new ArrayList<>();
        if (dataSourceInfo == null || dataSourceInfo.getGroupName() == null || dataSourceInfo.getDbUserName() == null ||
                dataSourceInfo.getDbPassword() == null || dataSourceInfo.getDataBaseName() == null || dataSourceInfo.getDbPort() == null ||
                dataSourceInfo.getDbType() == null || dataSourceInfo.getScheme() == null) {
            CheckSqlResult checkSqlResult = new CheckSqlResult();
            checkSqlResult.setFileName(file.getName());
            checkSqlResult.setTips("该文件所在的数据源组信息配置不完整，请检查！");
            results.add(checkSqlResult);
            return results;
        }

        if ("db2".equalsIgnoreCase(dataSourceInfo.getDbType()) && (dataSourceInfo.getServerIp() == null ||
                dataSourceInfo.getServerUserName() == null || dataSourceInfo.getServerPassword() == null)) {
            CheckSqlResult checkSqlResult = new CheckSqlResult();
            checkSqlResult.setFileName(file.getName());
            checkSqlResult.setTips("该文件所在的（DB2）数据源组信息配置缺少物理机信息，请检查！");
            results.add(checkSqlResult);
            return results;
        }

        //获取数据库类型
        String dbType = dataSourceInfo.getDbType();
        //获取xml中的sql
        Map<String, String> sqlMap = XmlUtils.getSql(file);

        for (Map.Entry<String, String> entry : sqlMap.entrySet()) {
            String primarySql = entry.getValue();
            try {
                //获取freemarker转换后的sql
                Map<String, Object> paramMap = new HashMap<>();
                String sql = getSqlFromFreemarker(primarySql, paramMap);
                if (StringUtils.isEmpty(sql)) {
                    CheckSqlResult checkSqlResult = new CheckSqlResult();
                    checkSqlResult.setFileName(file.getName());
                    checkSqlResult.setSqlId(entry.getKey());
                    checkSqlResult.setTips(paramMap.get("errMsg").toString());
                    checkSqlResult.setSql(primarySql);
                    results.add(checkSqlResult);
                    continue;
                }
                ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);
                //真实sql
                String preparedSql = NamedParameterUtils.substituteNamedParameters(parsedSql, new MapSqlParameterSource()).trim();
                System.out.println("预编译侯的sql====>" + preparedSql);
                //获取表名、字段名
                SQLStatementParser sqlStatementParser;
                // 使用visitor来访问AST
                SchemaStatVisitor visitor;
                String tableConstructSql;
                if ("mysql".equalsIgnoreCase(dbType)) {
                    sqlStatementParser = new MySqlStatementParser(sql);
                    visitor = new MySqlSchemaStatVisitor();
                    tableConstructSql = "select column_name,data_type from information_schema.columns where table_name =";
                } else if ("db2".equalsIgnoreCase(dbType)) {
                    sqlStatementParser = new DB2StatementParser(sql.replaceAll("(?i)with.*ur", ""));
                    visitor = new DB2SchemaStatVisitor();
                    tableConstructSql = "select name,coltype from sysibm.syscolumns where tbname =";
                } else {
                    throw new RuntimeException("checksql 目前只支持mysql和DB2");
                }
                //使用sqlStatementParser 将sql解析成 AST
                SQLStatement sqlStatement = sqlStatementParser.parseStatement();
                sqlStatement.accept(visitor);
                //获取表
                //用于保存表结构
                Map<String, Object> tableConstructMap = new CaseInsensitiveMap();
                Map<TableStat.Name, TableStat> tables = visitor.getTables();
                String operatType = "";
                for (Map.Entry<TableStat.Name, TableStat> tableStatEntry : tables.entrySet()) {
                    TableStat.Name key = tableStatEntry.getKey();
                    String tableName = key.getName();
                    if (tableName.contains(".")) {
                        tableName = tableName.split("\\.")[1];
                    }
                    //查询表结构信息
                    Map<String, Object> tempMap = DbUtils.queryTableConstruct(dataSourceInfo,
                            tableConstructSql + "'" + tableName.toUpperCase() + "'");
                    tableConstructMap.putAll(tempMap);
                    operatType = tableStatEntry.getValue().toString();
                }
                //说明没有这张表
                if (tableConstructMap.isEmpty()) {
                    CheckSqlResult checkSqlResult = new CheckSqlResult();
                    checkSqlResult.setFileName(file.getName());
                    checkSqlResult.setSqlId(entry.getKey());
                    checkSqlResult.setSql(primarySql);
                    checkSqlResult.setTips("该表不存在，请核实！");
                    results.add(checkSqlResult);
                    continue;
                }

                //sql初始参数，用于生成具体sql
                Map<String, Object> params = new CaseInsensitiveMap();
                //用于记录异常，是否继续
                boolean flag = true;
                //select 和 delete类型的 获取条件字段
                if ("Select".equalsIgnoreCase(operatType) || "Delete".equalsIgnoreCase(operatType) || "Update".equalsIgnoreCase(operatType)) {
                    List<TableStat.Condition> conditions = visitor.getConditions();
                    for (TableStat.Condition condition : conditions) {
                        //条件字段名
                        String columnName = condition.getColumn().getName();
                        if (Pattern.matches("[\\s\\S]*(?i)" + columnName + "\\s*=\\s*\\?[\\s\\S]*", preparedSql)) {
                            if (!tableConstructMap.containsKey(columnName)) {
                                CheckSqlResult checkSqlResult = new CheckSqlResult();
                                checkSqlResult.setFileName(file.getName());
                                checkSqlResult.setSqlId(entry.getKey());
                                checkSqlResult.setSql(primarySql);
                                checkSqlResult.setTips("该表中不存在字段：" + columnName);
                                results.add(checkSqlResult);
                                flag = false;
                                break;
                            }
                            //获取对应的字段类型
                            String columnType = tableConstructMap.get(columnName) == null ? "" : tableConstructMap.get(columnName).toString();
                            params.put(columnName.trim(), CheckSqlConstant.SQLDEFUALVALUE.get(columnType));
                        }
                    }
                }

                if (!flag) {
                    continue;
                }

                if ("Update".equalsIgnoreCase(operatType)) {
                    //insert 和 update获取 所有字段
                    Collection<TableStat.Column> columns = visitor.getColumns();
                    for (TableStat.Column column : columns) {
                        String columnName = column.getName();
                        //获取对应的字段类型
                        String columnType = tableConstructMap.get(columnName) == null ? "" : tableConstructMap.get(columnName).toString();
                        params.put(columnName.trim(), CheckSqlConstant.SQLDEFUALVALUE.get(columnType));
                    }
                }
                String column;
                preparedSql = preparedSql.replaceAll("[,]", ", ");
                while ((column = getColumn(preparedSql.replaceAll("[\n\t]", " "))) != null) {
                    String value = params.get(column) == null ? "'232'" : params.get(column).toString();
                    if ("".equals(value)) {
                        LOGGER.error("获取默认字段值异常，文件名：{}，sqlId:{},字段名：{}", file.getName(), entry.getKey(), column);
                    }
                    preparedSql = preparedSql.replaceFirst("\\?", value);
                }

                //处理分页
                if (dbType.equalsIgnoreCase("mysql") && Pattern.matches(".*(?i)limit.*", preparedSql.replaceAll("[\n,\r]", ""))) {
                    preparedSql = preparedSql.replaceAll("\\?", "1");
                } else if (dbType.equalsIgnoreCase("db2") && Pattern.matches(".*(?i)rowid_.*", preparedSql.replaceAll("[\n,\r]", ""))) {
                    preparedSql = preparedSql.replaceAll("\\?", "1");
                }

                //如果还有？ 就用空字符替换
                if (preparedSql.contains("?")) {
                    preparedSql = preparedSql.replaceAll("\\?", "'111'");
                }

                System.out.println("实际的sql======>" + preparedSql);
                //sql执行计划
                List<CheckSqlResult> checkSqlResults = null;
                if ("mysql".equalsIgnoreCase(dbType)) {
                    List<Map<String, Object>> queryDataList = DbUtils.getQueryDataList(dataSourceInfo, "explain " + preparedSql, null);
                    checkSqlResults = analyseMysqlExplain(file.getName(), entry, visitor, queryDataList);
                } else if ("db2".equalsIgnoreCase(dbType)) {
                    checkSqlResults = analyseDB2Explain(file.getName(), entry, visitor, dataSourceInfo, preparedSql);
                }
                results.addAll(checkSqlResults);
            } catch (Exception e) {
                LOGGER.error("checksql 检查:{}，时异常：{}，sql：{}", file.getName(), e.getMessage(), primarySql);
                CheckSqlResult checkSqlResult = new CheckSqlResult();
                checkSqlResult.setFileName(file.getName());
                checkSqlResult.setSql(primarySql);
                checkSqlResult.setSqlId(entry.getKey());
                checkSqlResult.setTips("sql执行异常：" + e.getMessage());
                results.add(checkSqlResult);
            }
        }
        return results;
    }

    private List<CheckSqlResult> analyseDB2Explain(String fileName, Map.Entry<String, String> entry, SchemaStatVisitor visitor, DataSourceInfo dataSourceInfo, String preparedSql) {
        //分析结果
        List<CheckSqlResult> checkSqlResults = new ArrayList<>();
        //数据库名
        String database = dataSourceInfo.getDataBaseName();

        //添加scheme，并去掉换行符
        String finalSql = addSchemaToSql(preparedSql, dataSourceInfo.getScheme(), visitor);
        String execute = LinuxUtil.execCmd("db2expln -d  " + database + " -i -q \"" + finalSql + "\" -t",
                dataSourceInfo, 22);

        List<String> tables = new ArrayList<>();
        List<String> indices = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(execute.getBytes())));
        String line;
        String table = "";
        String index = "";
        try {
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if ("".equals(line.trim())) {
                    continue;
                }
                //预读，会重复出现已经解析过的表名，导致解析成 没有用到索引
                if (line.contains("List Prefetch Preparation")) {
                    return checkSqlResults;
                }
                if (line.contains("Error compiling statement")) {
                    CheckSqlResult checkSqlResult = new CheckSqlResult();
                    checkSqlResult.setFileName(fileName);
                    checkSqlResult.setSqlId(entry.getKey());
                    checkSqlResult.setSql(entry.getValue());
                    checkSqlResult.setTips("该SQL语法错误，请检查(如：SQL里不能指定scheme)！");
                    checkSqlResults.add(checkSqlResult);
                    return checkSqlResults;
                }
                //Access Table Name = SCSUSER.WORKFLOWREG  ID = 3,11
                if (line.contains("Access Table Name")) {
                    String[] split = line.split("=");
                    String temp = split[1].trim();
                    table = temp.substring(0, temp.indexOf(" ")).trim();
                    tables.add(table);
                } else
                    //Index Scan:  Name = SCSUSER.WORKFLOWREG_IDX2  ID = 2
                    if (line.contains("Index Scan")) {
                        String[] split = line.split("=");
                        String temp = split[1].trim();
                        index = temp.substring(0, temp.indexOf(" ")).trim();
                        indices.add(index);
                        table = "";
                    } else if (!"".equals(table)) {
                        index = "";
                        indices.add(index);
                    }
            }
            CheckSqlResult checkSqlResult;
            //循环遍历tables
            for (int i = 0; i < tables.size(); i++) {
                //表名
                String tableTemp = tables.get(i);
                String indexTemp = indices.get(i);
                checkSqlResult = new CheckSqlResult();
                checkSqlResult.setFileName(fileName);
                checkSqlResult.setSqlId(entry.getKey());
                checkSqlResult.setSql(entry.getValue());
                checkSqlResult.setIndex(indexTemp);
                if ("".equals(indexTemp)) {
                    checkSqlResult.setTips(tableTemp + ":没有使用到索引，请优化SQL");
                } else {
                    continue;
                }
                checkSqlResults.add(checkSqlResult);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return checkSqlResults;
    }


    /**
     * 分析mysql的执行计划
     *
     * @param fileName
     * @param explainList
     */
    private List<CheckSqlResult> analyseMysqlExplain(String fileName, Map.Entry<String, String> entry, SchemaStatVisitor visitor, List<Map<String, Object>> explainList) {
        //分析结果
        List<CheckSqlResult> checkSqlResults = new ArrayList<>();
        CheckSqlResult checkSqlResult;
        StringBuilder tips;
        boolean flag = true;
        for (Map<String, Object> map : explainList) {
            checkSqlResult = new CheckSqlResult();
            tips = new StringBuilder();
            //主键
            String key = map.get("key") == null ? "" : map.get("key").toString().trim();
            //额外的信息
            String extra = map.get("extra") == null ? "" : map.get("extra").toString().trim();
            //查询类型
            String selectType = map.get("selectType") == null ? "" : map.get("selectType").toString().trim();
            //设置文件名
            checkSqlResult.setFileName(fileName);
            //sqlId
            checkSqlResult.setSqlId(entry.getKey());
            //SQL
            checkSqlResult.setSql(entry.getValue());
            //使用到的索引
            checkSqlResult.setIndex(key);
            if ("".equals(key)) {
                tips.append("该查询类型：").append(selectType).append("没有使用到索引，请优化SQL;");
                flag = false;
            }
            if (extra.contains("filesort")) {
                tips.append("MySQL 会对数据使用一个外部的索引排序，而不是按照表内的索引顺序进行读取，请优化");
                flag = false;
            }
            if (extra.contains("temporary")) {
                tips.append("MySQL 在对查询结果排序时使用临时表，请优化 SQL。常见于排序 order by 和分组查询 group by。 ");
                flag = false;
            }
            if (selectType.equalsIgnoreCase("delete") && visitor.getConditions().size() == 0) {
                tips.append("delete时没有添加where条件，会删除整张表，请谨慎操作！ ");
                flag = false;
            }
            if (flag) {
                continue;
            }
            checkSqlResult.setTips(tips.toString());
            checkSqlResults.add(checkSqlResult);
        }
        return checkSqlResults;
    }

    public String getSqlFromFreemarker(String primarySql, Map<String, Object> paramMap) {
        String sql = "";
        try {
            Template template = new Template(DEFAULT_TEMPLATE_KEY, new StringReader(primarySql), CONFIGURER);
            template.setNumberFormat("#");
            StringWriter out = new StringWriter();
            template.process(paramMap, out);
            sql = out.toString();
        } catch (Exception e) {
            LOGGER.error("checksql转换freemarker sql模板异常：{}", e.getMessage());
            paramMap.put("errMsg", "checksql转换freemarker sql模板异常：{}" + e.getMessage());
        }
        return sql;
    }

    private DataSourceInfo getDataSourceInfo(String fileName) {
        DataSourceInfo dataSourceInfo = null;
        for (DataSourceInfo dsInfo : dataSourceInfos) {
            String mapperNames = dsInfo.getMapperNames();
            if (mapperNames != null && mapperNames.contains(fileName)) {
                dataSourceInfo = dsInfo;
                break;
            }
        }
        return dataSourceInfo;
    }

    private String addSchemaToSql(String sql, String schema, SchemaStatVisitor visitor) {
        //获取所有的表名
        sql = sql.replaceAll("\n", "").replaceAll("\\t", " ");
        Map<TableStat.Name, TableStat> tables = visitor.getTables();
        for (Map.Entry<TableStat.Name, TableStat> entry : tables.entrySet()) {
            String tableName = entry.getKey().getName();
            sql = sql.replaceAll(" (?i)" + tableName, " " + schema + "." + tableName + " ");
        }
        return sql;
    }


    private String getColumn(String preparedSql) {
        String column = null;
        Pattern compile = Pattern.compile("\\s*(\\S*)\\s*=\\s*\\?");
        Matcher matcher = compile.matcher(preparedSql.replaceAll("\\s", " "));
        if (matcher.find()) {
            String group = matcher.group();
            String tempColumn = group.split("=")[0].replaceAll("[\\s><]", "");
            if (tempColumn.contains(".")) {
                column = tempColumn.split("\\.")[1];
            } else {
                column = tempColumn;
            }
            System.out.println(group.split("=")[0]);
        }
        return column;
    }
}
