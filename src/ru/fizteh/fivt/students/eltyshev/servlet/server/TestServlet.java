package ru.fizteh.fivt.students.eltyshev.servlet.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TestServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF8");

        resp.getWriter().println("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Servlet Tester</title>\n" +
                "    <script type=\"text/javascript\">\n" +
                "        function getXmlHttp()\n" +
                "{\n" +
                "      var xmlhttp;\n" +
                "      try {\n" +
                "        xmlhttp = new ActiveXObject('Msxml2.XMLHTTP');\n" +
                "      } catch (e) {\n" +
                "        try {\n" +
                "          xmlhttp = new ActiveXObject('Microsoft.XMLHTTP');\n" +
                "        } catch (E) {\n" +
                "          xmlhttp = false;\n" +
                "        }\n" +
                "      }\n" +
                "      if (!xmlhttp && typeof XMLHttpRequest!='undefined') {\n" +
                "        xmlhttp = new XMLHttpRequest();\n" +
                "      }\n" +
                "      return xmlhttp;\n" +
                "}\n" +
                "\n" +
                "function get(id)\n" +
                "{\n" +
                "\treturn document.getElementById(id).value\n" +
                "}\n" +
                "\n" +
                "function get_url(action)\n" +
                "{\n" +
                "    switch(action)\n" +
                "    {\n" +
                "        case \"begin\":\n" +
                "            return \"/begin?table=\" + document.getElementById(\"tableName\").value;\n" +
                "        case \"get\":\n" +
                "            return \"/get?tid=\" + get(\"get_transaction_id\") + \"&key=\" + get(\"get_key\");\n" +
                "        case \"put\":\n" +
                "            return \"/put?tid=\" + get(\"put_transaction_id\") +\n" +
                "                                \"&key=\" + get(\"put_key\") + \"&value=\" + get(\"put_value\");\n" +
                "        default:\n" +
                "            return \"/\" + action + \"?tid=\" + get(action + \"_transaction_id\");\n" +
                "    }\n" +
                "}\n" +
                "function onAction(action)\n" +
                "{\n" +
                "\tvar url = get_url(action);\n" +
                "\tvar xmlHttp = getXmlHttp();\n" +
                "\txmlHttp.open('GET', url, true);\n" +
                "\n" +
                "\txmlHttp.onreadystatechange = function()\n" +
                "\t{\n" +
                "\t\tif (xmlHttp.readyState != 4)\n" +
                "\t\t\treturn;\n" +
                "\t\tvar div = document.getElementById('content');\n" +
                "\t\tif (xmlHttp.status == 200)\n" +
                "\t\t{\n" +
                "\t\t\tdiv.innerText = xmlHttp.responseText;\n" +
                "\t\t}\n" +
                "\t\telse\n" +
                "\t\t{\n" +
                "\t\t\tdiv.innerHTML = xmlHttp.responseText;\n" +
                "\t\t}\n" +
                "\t}\n" +
                "\n" +
                "\txmlHttp.send();\n" +
                "}\n" +
                "    </script>\n" +
                "    <style>\n" +
                "        .controlButton\n" +
                "        {\n" +
                "        width: 257px;\n" +
                "        height: 30px;\n" +
                "        }\n" +
                "\n" +
                "        label{\n" +
                "        display: inline-block;\n" +
                "        float: left;\n" +
                "        clear: left;\n" +
                "        width: 100px;\n" +
                "        text-align: right;\n" +
                "        }\n" +
                "\n" +
                "        input {\n" +
                "        display: inline-block;\n" +
                "        float: top-left;\n" +
                "        }\n" +
                "\n" +
                "        td\n" +
                "        {\n" +
                "        padding: 3;\n" +
                "        vertical-align: top;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div id=\"contols\">\n" +
                "    <table>\n" +
                "        <tr>\n" +
                "            <td>\n" +
                "                <button type=\"button\" class=\"controlButton\" onclick=\"onAction('begin');\">Begin transaction</button>\n" +
                "            </td>\n" +
                "            <td>\n" +
                "                <button type=\"button\" class=\"controlButton\" onclick=\"onAction('get');\">Get</button>\n" +
                "            </td>\n" +
                "            <td>\n" +
                "                <button type=\"button\" class=\"controlButton\" onclick=\"onAction('put');\">Put</button>\n" +
                "            </td>\n" +
                "            <td>\n" +
                "                <button type=\"button\" class=\"controlButton\" onclick=\"onAction('commit');\">Commit</button>\n" +
                "            </td>\n" +
                "            <td>\n" +
                "                <button type=\"button\" class=\"controlButton\" onclick=\"onAction('rollback');\">Rollback</button>\n" +
                "            </td>\n" +
                "            <td>\n" +
                "                <button type=\"button\" class=\"controlButton\" onclick=\"onAction('size');\">Size</button>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td>\n" +
                "                <label> Table name: </label>\n" +
                "                <input class=\"inputField\" type=\"text\" id=\"tableName\" value=\"table\">\n" +
                "            </td>\n" +
                "            <td>\n" +
                "                <label> Transaction id: </label>\n" +
                "                <input class=\"inputField\" type=\"text\" id=\"get_transaction_id\" maxlength=\"5\" size=\"5\">  <br>\n" +
                "                <label>Key: </label>\n" +
                "                <input class=\"inputField\" type=\"text\" id=\"get_key\">\n" +
                "            </td>\n" +
                "            <td>\n" +
                "                <label> Transaction id: </label>\n" +
                "                <input class=\"inputField\" type=\"text\" id=\"put_transaction_id\" maxlength=\"5\" size=\"5\">  <br>\n" +
                "                <label>Key: </label>\n" +
                "                <input class=\"inputField\" type=\"text\" id=\"put_key\">\n" +
                "                <label>Value: </label>\n" +
                "                <input class=\"inputField\" type=\"text\" id=\"put_value\">\n" +
                "            </td>\n" +
                "            <td>\n" +
                "                <label> Transaction id: </label>\n" +
                "                <input class=\"inputField\" type=\"text\" id=\"commit_transaction_id\" maxlength=\"5\" size=\"5\">\n" +
                "            </td>\n" +
                "            <td>\n" +
                "                <label> Transaction id: </label>\n" +
                "                <input class=\"inputField\" type=\"text\" id=\"rollback_transaction_id\" maxlength=\"5\" size=\"5\">\n" +
                "            </td>\n" +
                "            <td>\n" +
                "                <label> Transaction id: </label>\n" +
                "                <input class=\"inputField\" type=\"text\" id=\"size_transaction_id\" maxlength=\"5\" size=\"5\">\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "</div>\n" +
                "<div id=\"content\">\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>");
    }
}
