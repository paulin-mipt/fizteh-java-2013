package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CommandBeginServlet extends HttpServlet {
    TransactionWorker manager;

    public CommandBeginServlet(TransactionWorker manager) {
        this.manager = manager;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("table");
        if (name == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "There is no table name");
            return;
        }
        String tId = manager.startTransaction(name);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF8");
        response.getWriter().println("tid=" +  tId);
    }
}
