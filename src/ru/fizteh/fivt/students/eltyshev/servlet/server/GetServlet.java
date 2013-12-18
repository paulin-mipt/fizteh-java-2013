package ru.fizteh.fivt.students.eltyshev.servlet.server;

import ru.fizteh.fivt.students.eltyshev.servlet.database.Transaction;
import ru.fizteh.fivt.students.eltyshev.servlet.database.TransactionManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GetServlet extends HttpServlet {
    private TransactionManager manager;

    public GetServlet(TransactionManager manager) {
        this.manager = manager;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String transactionId = req.getParameter(Constants.TRANSACTION_ID);
        if (transactionId == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Transaction id expected");
            return;
        }

        String key = req.getParameter(Constants.KEY);
        if (key == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Key expected");
            return;
        }

        Transaction transaction = manager.getTransaction(transactionId);
        if (transaction == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Transaction not found");
            return;
        }

        try {
            String value = transaction.get(key);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("text/plain");
            resp.setCharacterEncoding("UTF8");

            resp.getWriter().println(value);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
