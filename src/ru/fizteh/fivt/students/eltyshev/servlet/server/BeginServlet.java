package ru.fizteh.fivt.students.eltyshev.servlet.server;

import ru.fizteh.fivt.students.eltyshev.servlet.database.TransactionManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BeginServlet extends HttpServlet {
    TransactionManager manager;

    public BeginServlet(TransactionManager manager) {
        this.manager = manager;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String tableName = req.getParameter(ParamNames.TABLE_NAME.name);
        if (tableName == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Table name expected");
            return;
        }

        String transactionId = manager.beginTransaction(tableName);
        resp.setStatus(HttpServletResponse.SC_OK);

        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF8");

        resp.getWriter().println(String.format("%s=%s", ParamNames.TRANSACTION_ID.name, transactionId));
    }
}
