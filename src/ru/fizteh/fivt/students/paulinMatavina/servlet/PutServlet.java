package ru.fizteh.fivt.students.paulinMatavina.servlet;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.paulinMatavina.filemap.MyTable;

@SuppressWarnings("serial")
public class PutServlet extends HttpServlet {
    private Database database;
    public PutServlet(Database newBase) {
        database = newBase;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (ServletUtils.checkParameters(database, request, response) != 0) {
            return;
        }
        String tid = request.getParameter("tid");
        String key = request.getParameter("key");
        String strValue = request.getParameter("value");
        if (key == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "no key passed");
            return;
        }
        if (strValue == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "no value passed");
            return;
        }
        Database.Transaction transaction = database.getTransaction(tid);
        if (transaction == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "no transaction " + tid + " found");
            return;
        }
        transaction.start();
        try {
            MyTable table = transaction.getTable();
            Storeable value;
            value = table.provider.deserialize(table, strValue);
            Storeable storedValue = transaction.getTable().put(key, value);
            if (storedValue == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "no previous value found");
            }
            ServletUtils.sendInfo(response, storedValue);
        } catch (Throwable e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            transaction.end();
        }
    }
}
