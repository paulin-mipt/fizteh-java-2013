package ru.fizteh.fivt.students.kochetovnicolai.fileMap.servletCommands;

import ru.fizteh.fivt.students.kochetovnicolai.fileMap.DistributedTable;
import ru.fizteh.fivt.students.kochetovnicolai.fileMap.TableManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServletCommandSize extends ServletCommand {
    TableManager manager;

    public ServletCommandSize(TableManager manager) {
        super(manager);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        DistributedTable table = getTable(req, resp);
        if (table == null) {
            return;
        }

        int size;
        try {
            table.useTransaction(sessionID);
            size = table.size();
        } catch (IllegalStateException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            manager.deleteTableByID(sessionID);
            return;
        } finally {
            table.setDefaultTransaction();
        }
        resp.setStatus(HttpServletResponse.SC_OK);

        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF8");
        resp.getWriter().println(size);
    }
}
