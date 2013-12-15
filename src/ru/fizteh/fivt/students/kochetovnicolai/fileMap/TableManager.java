package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.kochetovnicolai.fileMap.servletCommands.*;
import ru.fizteh.fivt.students.kochetovnicolai.shell.Manager;

import javax.servlet.Servlet;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

public class TableManager extends Manager {

    private DistributedTable currentTable = null;
    private DistributedTableProvider provider;
    private HashMap<String, DistributedTable> tables;
    private Server server = null;

    private HashMap<String, Servlet> servletCommands = null;
    private int port;
    private HashMap<Integer, DistributedTable> servletTables;
    final int maxID = 100000;

    private void initialiseServletCommands() {
        servletCommands = new HashMap<>();
        servletCommands.put("/begin", new ServletCommandBegin(this));
        servletCommands.put("/commit", new ServletCommandCommit(this));
        servletCommands.put("/get", new ServletCommandGet(this));
        servletCommands.put("/rollback", new ServletCommandRollback(this));
        servletCommands.put("/put", new ServletCommandPut(this));
        servletCommands.put("/size", new ServletCommandSize(this));
    }

    public boolean startHTTP(int port) {
        if (server != null) {
            printMessage("not started: already started");
            return false;
        }

        server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        if (servletCommands == null) {
            initialiseServletCommands();
        }
        for (String command: servletCommands.keySet()) {
            context.addServlet(new ServletHolder(servletCommands.get(command)), command);
        }
        server.setHandler(context);

        try {
            server.start();
        } catch (Exception e) {
            printMessage("not started: " + server.getState());
            server = null;
            return false;
        }
        this.port = port;
        printMessage("started at " + port);
        return true;
    }

    public boolean stopHTTP() {
        if (server == null) {
            printMessage("not started");
            return false;
        }
        try {
            server.stop();
        } catch (Exception e) {
            printMessage("not started");
            return false;
        }
        servletTables.clear();
        printMessage("stopped at " + port);
        server = null;
        return true;
    }

    public Integer newSession(String tableName) throws IllegalStateException {
        DistributedTable table = getTable(tableName);
        if (table == null) {
            return null;
        }
        for (int i = 0; i < maxID; i++) {
            if (!servletTables.containsKey(i)) {
                servletTables.put(i, table);
                return i;
            }
        }
        throw new IllegalStateException("server is busy");
    }

    public DistributedTable getTableByID(int sessionID) {
        return servletTables.get(sessionID);
    }

    public boolean deleteTableByID(int sessionID) {
        return servletTables.remove(sessionID) != null;
    }

    public boolean existsTable(String name) {
        if (!tables.containsKey(name)) {
            try {
                return provider.existsTable(name);
            } catch (IllegalArgumentException e) {
                printMessage(e.getMessage());
                return false;
            }
        }
        return tables.containsKey(name);
    }

    public TableManager(DistributedTableProvider provider) {
        this.provider = provider;
        tables = new HashMap<>();
        servletTables = new HashMap<>();
    }

    public void setCurrentTable(DistributedTable table) {
        currentTable = table;
    }

    public DistributedTable getTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("table name shouldn't be null");
        }
        if (!tables.containsKey(name)) {
            DistributedTable table = provider.getTable(name);
            if (table != null) {
                tables.put(name, table);
            }
        }
        return tables.get(name);
    }

    public DistributedTable createTable(String name, List<Class<?>> columnTypes) throws IllegalArgumentException {
        if (name == null || columnTypes == null) {
            throw new IllegalArgumentException("table name shouldn't be null");
        }
        if (!tables.containsKey(name)) {
            try {
                tables.put(name, provider.createTable(name, columnTypes));
                if (tables.get(name) == null) {
                   tables.put(name, provider.getTable(name));
                }
            } catch (IllegalArgumentException e) {
                printMessage(e.getMessage());
            } catch (IOException e) {
                printMessage("couldn't create table: " + e.getMessage());
            }
        }
        return tables.get(name);
    }

    public boolean removeTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("table name shouldn't be null");
        }
        if (currentTable == tables.get(name)) {
            currentTable = null;
        }
        tables.remove(name);
        try {
            provider.removeTable(name);
        } catch (IllegalArgumentException e) {
            printMessage(e.getMessage());
            return false;
        } catch (IOException e) {
            printMessage(e.getMessage());
            return false;
        }
        return true;
    }

    public DistributedTable getCurrentTable() {
        return currentTable;
    }

    public String serialize(Storeable storiable) throws ParseException {
        return provider.serialize(currentTable, storiable);
    }

    public Storeable deserialize(String string) throws ColumnFormatException, ParseException {
        return provider.deserialize(currentTable, string);
    }

    @Override
    public void printSuggestMessage() {
        if (currentTable != null) {
            outputStream.print(currentTable.getName());
        }
        outputStream.print(" $ ");
    }

    @Override
    public void setExit() {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                printMessage(e.getMessage());
            }
        }
        super.setExit();
    }
}
