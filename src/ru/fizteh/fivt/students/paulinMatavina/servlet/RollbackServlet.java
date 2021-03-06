package ru.fizteh.fivt.students.paulinMatavina.servlet;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class RollbackServlet extends HttpServlet {
    private Database database;
    public RollbackServlet(Database newBase) {
        database = newBase;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (ServletUtils.checkParameters(database, request, response) != 0) {
            return;
        }
        String tid = request.getParameter("tid");
        Database.Transaction transaction = database.getTransaction(tid);
        if (transaction == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "no transaction " + tid + " found");
            return;
        }
        try {
            int changeNum = transaction.getTable().rollback();
            ServletUtils.sendInfo(response, "diff=" + changeNum);
            database.cancelTransaction(tid);
        } catch (Throwable e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}

