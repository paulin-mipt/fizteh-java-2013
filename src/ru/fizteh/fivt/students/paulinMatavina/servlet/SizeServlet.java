package ru.fizteh.fivt.students.paulinMatavina.servlet;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class SizeServlet extends HttpServlet {
    private Database database;
    public SizeServlet(Database newBase) {
        database = newBase;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletUtils.checkParameters(database, request, response);
        String tid = request.getParameter("tid");
        Database.Transaction transaction = database.getTransaction(tid);
        transaction.start();
        try {
            Integer size = new Integer(transaction.getTable().size());
            ServletUtils.sendInfo(response, size);
        } catch (Throwable e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            transaction.end();
        }
    }
}
