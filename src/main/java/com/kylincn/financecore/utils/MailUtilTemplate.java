package com.suning.financecore.utils;

import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 邮件发送工具类
 *
 * @author 18042621
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class MailUtilTemplate {

    /**
     * 根据模板名称查找模板，加载模板内容后发送邮件
     *
     * @param receiver     收件人地址
     * @param subject      邮件主题
     * @param map          邮件内容与模板内容转换对象
     * @param templateName 模板文件名称
     * @return void
     * @throws IOException
     * @throws TemplateException
     * @throws MessagingException
     * @Description:
     */
    public static void sendMailByTemplate(String receiver, String subject,
                                          Map<String, Object> map, String templateName) throws IOException,
            TemplateException, MessagingException {
        //邮件服务中心
        String smtp = CheckSqlConstant.MAIL_SERVER;

        MailSender mail = new MailSender(smtp);
        mail.setNeedAuth(true);
        mail.setNamePass(CheckSqlConstant.MAIL_USER, CheckSqlConstant.MAIL_PASSWORD, CheckSqlConstant.MAIL_NICKNAME);
        String maiBody = TemplateFactory.generateHtmlFromFtl(templateName, map);
        mail.setSubject(subject);
        mail.setBody(maiBody);
        mail.setReceiver(receiver);
        mail.setSender(CheckSqlConstant.MAIL_SENDER);
        mail.sendout();
    }
}
