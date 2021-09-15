<html>
<h2>${sysName}系统SQL检测报告</h2>
<#if dataList??&&dataList?size gt 0>
    <table border="1" cellpadding="0" cellspacing="0" style="word-break: break-all">
        <tr>
            <th width="15%">文件名</th>
            <th width="10%">SQLID</th>
            <th width="30%">SQL</th>
            <th width="15%">索引</th>
            <th width="30%">提示</th>
        </tr>
        <#list dataList as data>
            <tr>
                <td width="15%">${data.fileName!""}</td>
                <td width="10%">${data.sqlId!""}</td>
                <td width="30%">${data.sql!""}</td>
                <td width="15%">${data.index!""}</td>
                <td width="30%">${data.tips!""}</td>
            </tr>
        </#list>
    </table>
<#else>
    恭喜您，该系统SQL检测没有发现异常！
</#if>


</html>