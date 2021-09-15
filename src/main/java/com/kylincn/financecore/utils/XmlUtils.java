package com.kylincn.financecore.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * description: XmlUtils <br>
 * date: 2019/12/12 19:22 <br>
 * author: 18042621 <br>
 * version: 1.0 <br>
 */
public class XmlUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlUtils.class);

    public static Map<String, String> getSql(File file) {
        Map<String, String> sqlMap = new LinkedHashMap<>();
        try {
            InputStream inputStream = new FileInputStream(file);
            //创建解析工厂
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            //创建解析器
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            //通过解析器读取配置文件,生成一个Document[org.w3c.dom]对象树
            Document document = builder.parse(inputStream);
            //创建XPath对象
            XPath xPath = XPathFactory.newInstance().newXPath();
            // 获取bookstore节点下所有book的节点集合
            NodeList sqlNodeList = (NodeList) xPath.evaluate("/sqlMap/sql", document, XPathConstants.NODESET);
            for (int i = 0; i < sqlNodeList.getLength(); i++) {
                Node item = sqlNodeList.item(i);
                String textContent = item.getTextContent();
                //insert不去校验
                if (textContent.toUpperCase().contains("INSERT")) {
                    continue;
                }
                String id = item.getAttributes().getNamedItem("id").getTextContent();
                sqlMap.put(id, textContent);
            }
        } catch (Exception e) {
            LOGGER.error("checksql读取mapper文件异常：{},文件：文件：{}", e.getStackTrace(), file.getName());
        }
        return sqlMap;
    }
}
