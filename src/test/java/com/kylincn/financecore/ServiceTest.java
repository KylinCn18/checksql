package com.kylincn.financecore;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * description: ServiceTest <br>
 * date: 2019/12/25 11:27 <br>
 * author: 18042621 <br>
 * version: 1.0 <br>
 */
public class ServiceTest {


    @Test
    public void test01() {
        String preparedSql = "SELECT PAGE_ROW_.* FROM (SELECT ROWNUMBER() OVER() AS ROWID_,  ORGIN_TBL_.* FROM (\n" +
                "\t\t\t\tSELECT\n" +
                "\t\t\t\t    C.cSUBNAME ,\n" +
                "\t\t\t\t    D.dSUBNAME,\n" +
                "\t\t\t\t    E.accSubname,\n" +
                "\t\t\t\t    S.ROW_No,\n" +
                "\t\t\t\t    S.trancode,\n" +
                "\t\t\t\t    S.dsubcode,\n" +
                "\t\t\t\t    S.csubcode,\n" +
                "\t\t\t\t    S.subcode,\n" +
                "\t\t\t\t    S.profitbrc,\n" +
                "\t\t\t\t    S.COUNT,\n" +
                "\t\t\t\t    S.SUMAMT,\n" +
                "\t\t\t\t    A.ACCDESC,\n" +
                "\t\t\t\t    B.CAUSE\n" +
                "\t\t\t\tFROM\n" +
                "\t\t\t\t    (\n" +
                "\t\t\t\t        SELECT\n" +
                "\t\t\t\t            ROW_NUMBER() OVER() AS ROW_No,\n" +
                "\t\t\t\t            trancode,\n" +
                "\t\t\t\t            dsubcode,\n" +
                "\t\t\t\t            csubcode,\n" +
                "\t\t\t\t            subcode,\n" +
                "\t\t\t\t            profitbrc,\n" +
                "\t\t\t\t            COUNT(*)     AS COUNT,\n" +
                "\t\t\t\t            SUM(tranamt) AS SUMAMT\n" +
                "\t\t\t\t        FROM\n" +
                "\t\t\t\t            scadotranslog t\n" +
                "\t\t\t\t        WHERE\n" +
                "\t\t\t\t            STATUS IN ('0',\n" +
                "\t\t\t\t                       '2')\n" +
                "\t\t\t\t        AND t.TRANDATE = ?\n" +
                "\t\t\t\t        \t\t\t\t        GROUP BY\n" +
                "\t\t\t\t            t.trancode,\n" +
                "\t\t\t\t            t.dsubcode,\n" +
                "\t\t\t\t            t.csubcode,\n" +
                "\t\t\t\t            t.subcode,\n" +
                "\t\t\t\t            t.profitbrc ) AS S\n" +
                "\t\t\t\tLEFT JOIN\n" +
                "\t\t\t\t    (\n" +
                "\t\t\t\t        SELECT\n" +
                "\t\t\t\t            SUBCODE,\n" +
                "\t\t\t\t            SUBNAME AS cSUBNAME\n" +
                "\t\t\t\t        FROM\n" +
                "\t\t\t\t            SITSUBDICT ) AS C\n" +
                "\t\t\t\tON\n" +
                "\t\t\t\t    S.csubcode = C.SUBCODE\n" +
                "\t\t\t\tLEFT JOIN\n" +
                "\t\t\t\t    (\n" +
                "\t\t\t\t        SELECT\n" +
                "\t\t\t\t            SUBCODE,\n" +
                "\t\t\t\t            SUBNAME AS dSUBNAME\n" +
                "\t\t\t\t        FROM\n" +
                "\t\t\t\t            SITSUBDICT ) AS D\n" +
                "\t\t\t\tON\n" +
                "\t\t\t\t    S.dsubcode = D.SUBCODE\n" +
                "\t\t\t\tLEFT JOIN\n" +
                "\t\t\t\t    (\n" +
                "\t\t\t\t        SELECT\n" +
                "\t\t\t\t            SUBCODE,\n" +
                "\t\t\t\t            SUBNAME AS accSubname\n" +
                "\t\t\t\t        FROM\n" +
                "\t\t\t\t            SITSUBDICT ) AS E\n" +
                "\t\t\t\tON\n" +
                "\t\t\t\t    S.subcode = E.SUBCODE\n" +
                "\t\t\t\tLEFT JOIN\n" +
                "\t\t\t\t    (\n" +
                "                \t\tSELECT DISTINCT\n" +
                "                    \t\tACCCODE,\n" +
                "                    \t\tACCDESC\n" +
                "                \t\tFROM\n" +
                "                    \t\taesaccoconf) AS A\n" +
                "\t\t\t\tON\n" +
                "\t\t\t\t    A.ACCCODE = S.TRANCODE\n" +
                "\t\t\t\tLEFT JOIN\n" +
                "\t\t\t\t    scasapBusiCfg AS B\n" +
                "\t\t\t\tON\n" +
                "\t\t\t\t    (\n" +
                "\t\t\t\t        B.ACCCODE = S.TRANCODE\n" +
                "\t\t\t\t    AND B.dsubcode = S.dsubcode\n" +
                "\t\t\t\t    AND B.csubcode = S.csubcode\n" +
                "\t\t\t\t    AND B.subcode = S.subcode)\n" +
                "\t\t\t\tORDER BY\n" +
                "\t\t\t\t    S.ROW_No\n" +
                "\t\t\t) ORGIN_TBL_) PAGE_ROW_ WHERE PAGE_ROW_.ROWID_ >= (? + 1) AND PAGE_ROW_.ROWID_ <= (? + ?)";

        boolean matches = Pattern.matches(".*\\s*(?i)trandate\\s*=\\s*\\?\\s*.*", preparedSql);
        System.out.println(matches);

        String t =
                "\t\t\t\t            STATUS IN ('0',\n" +
                        "\t\t\t\t                       '2')\n" +
                        "\t\t\t\t        AND t.trandate = ?\n" +
                        "\t\t\t\t        \t\t\t\t        GROUP BY";

        String t1 = "\t\t,''  ._ ( * AND t.TRANDATE = ?\n";

        System.out.println(Pattern.matches("[\\s\\S]*(?i)trandate\\s=\\s\\?[\\s\\S]*", preparedSql));
    }


    @Test
    public void test02(){
        String sql = "UPDATE WORKFLOWREG            SET INTFSTAUTS ='11',ENDDATE= ?            WHERE SOAPROCESSID = ?              AND BUSINESSTEMPLATEID = ?              AND BUSINESSCODE = ?              AND SEQNO = ?";
        //Pattern compile = Pattern.compile("\\s*([a-zA-Z0-9.]*?)\\s*=\\*\\?");
        Pattern compile = Pattern.compile("\\s*(\\S*)\\s*=\\s*\\?");
        Matcher matcher = compile.matcher(sql.replaceAll(","," "));
        if (matcher.find()){
            String group = matcher.group(1);
            System.out.println(matcher.groupCount());
            System.out.println(group);
        }
    }
}
