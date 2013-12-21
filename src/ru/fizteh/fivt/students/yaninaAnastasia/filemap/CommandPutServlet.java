package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CommandPutServlet extends HttpServlet {
    private TransactionWorker worker;

    public CommandPutServlet(TransactionWorker worker) {
        this.worker = worker;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String transactionId = request.getParameter("tid");
        if (transactionId == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "There is no transaction id");
            return;
        }

        String key = request.getParameter("key");
        if (key == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Key expected");
            return;
        }

        String value = request.getParameter("value");
        if (value == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        Transaction transaction = worker.getTransaction(transactionId);
        if (transaction == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Transaction not found");
            return;
        }

        try {
            String oldValue = transaction.put(key, value);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF8");
            response.getWriter().println(oldValue);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
