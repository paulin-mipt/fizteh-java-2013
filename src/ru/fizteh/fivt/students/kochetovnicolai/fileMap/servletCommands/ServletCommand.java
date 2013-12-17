package ru.fizteh.fivt.students.kochetovnicolai.fileMap.servletCommands;

import ru.fizteh.fivt.students.kochetovnicolai.fileMap.DistributedTable;
import ru.fizteh.fivt.students.kochetovnicolai.fileMap.TableManager;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class ServletCommand extends HttpServlet {

    TableManager manager;
    Integer sessionID = null;

    public ServletCommand(TableManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("manager shouldn't be null");
        }
        this.manager = manager;
    }

    protected DistributedTable getTable(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = getValue("tid", req, resp);
        if (id == null) {
            return null;
        }
        if (!id.matches("[0-9]{5}")) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, id + ": invalid tid format");
            return null;
        }
        sessionID = Integer.parseInt(id);
        DistributedTable table = manager.getTableByID(sessionID);
        if (table == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, id + ": unused tid");
        }
        return table;
    }

    protected String getValue(String name, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String value = req.getParameter(name);
        if (value == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, name + " expected");
            return null;
        }
        return value;
    }
}
