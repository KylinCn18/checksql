package com.suning.financecore.utils;


import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.suning.financecore.bean.DataSourceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * description: LinuxUtil <br>
 * date: 2019/12/17 10:09 <br>
 * author: 18042621 <br>
 * version: 1.0 <br>
 */
public class LinuxUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxUtil.class);

    private static final Map<String, Session> sessionMap = new HashMap<>();

    //连接服务器
    private static Session connect(DataSourceInfo dataSourceInfo, int port) {
        Session session = sessionMap.get(dataSourceInfo.getGroupName());
        if (session == null) {
            try {
                JSch jsch = new JSch();
                //获取sshSession
                session = jsch.getSession(dataSourceInfo.getServerUserName(), dataSourceInfo.getServerIp(), port);
                //添加密码
                session.setPassword(dataSourceInfo.getServerPassword());
                Properties sshConfig = new Properties();
                //严格主机密钥检查
                sshConfig.put("StrictHostKeyChecking", "no");
                session.setConfig(sshConfig);
                //开启sshSession连接
                session.connect();
                LOGGER.info("Server connection successful.");
                sessionMap.put(dataSourceInfo.getGroupName(), session);
            } catch (JSchException e) {
                LOGGER.error("连接Linux服务器异常：{}", e);
            }
        }
        return session;
    }

    //执行相关命令
    public static String execCmd(String command, DataSourceInfo dataSourceInfo, int port) {

        BufferedReader reader;
        ChannelExec channelExec = null;
        //保存返账数据
        StringBuilder sb = new StringBuilder();
        if (command != null) {
            try {
                Session session = connect(dataSourceInfo, port);
                channelExec = (ChannelExec) session.openChannel("exec");
                // 设置需要执行的shell命令
                channelExec.setCommand(command);
                LOGGER.info("linux命令:" + command);
                channelExec.setInputStream(null);
                channelExec.setErrStream(System.err);
                channelExec.connect();
                //读数据
                InputStream inputStream = channelExec.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String buf;
                while ((buf = reader.readLine()) != null) {
                    sb.append(buf).append("\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != channelExec) {
                    channelExec.disconnect();
                }
            }
        }
        return sb.toString();
    }


    public static void closeSession() {
        if (!sessionMap.isEmpty()) {
            for (Map.Entry<String, Session> entry : sessionMap.entrySet()) {
                entry.getValue().disconnect();
            }
        }
    }


    public static void main(String[] args) {
        String sql = "select DISTINCT(businesscode),businesstemplateid,soaprocessid,drafterid,title\n" +
                "\t\t from  SCSUSER.workflowreg\n" +
                "\t\t where seqno>0\n" +
                "\t\t and intfstauts in ('3','5','P','F')\n" +
                "\t\t and recalltime < 3\n" +
                "\t\t with ur";
       /* String execCmd = LinuxUtil.execCmd("db2expln -d  scsdb -i -q  \"" + sql + "\"  -t", "scsinst", "jqV1KtKpdvPP", "10.27.95.179", 22);
        LinuxUtil.closeSession();
        System.out.println(execCmd);*/
    }
}

