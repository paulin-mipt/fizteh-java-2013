package ru.fizteh.fivt.students.paulinMatavina.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import ru.fizteh.fivt.storage.structured.*;

@SuppressWarnings("serial")
public class GetServlet extends HttpServlet {
    private Database database;
    public GetServlet(Database newBase) {
        database = newBase;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletUtils.checkParameters(database, request, response);
        String tid = request.getParameter("tid");
        String key = request.getParameter("key");
        if (key == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "no key passed");
            return;
        }
        Database.Transaction transaction = database.getTransaction(tid);
        transaction.start();
        try {
            Storeable value = transaction.getTable().get(key);
            if (value == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, key + " not found");
                return;
            }
            ServletUtils.sendInfo(response, value);
        } catch (Throwable e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            transaction.end();
        }
    }
}
