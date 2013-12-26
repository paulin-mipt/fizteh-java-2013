package ru.fizteh.fivt.students.paulinMatavina.servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import ru.fizteh.fivt.students.paulinMatavina.filemap.MyTableProvider;

public class MyServer {
    private Server jettyServer = new Server();
    private Database database;
    private boolean isStarted;
    private int portNum;
    public MyServer(MyTableProvider provider) {
        database = new Database(provider);
        isStarted = false;
    }
    
    public void stop() throws IllegalStateException {
        if (jettyServer == null || !jettyServer.isStarted()) {
            throw new IllegalStateException("server is not started");
        }
        try {
            jettyServer.stop();
        } catch (Throwable e) {
            //do nothing
        }
        jettyServer = null;
        isStarted = false;
    }

    public void start(int port) throws Exception {
        portNum = port;
        if (jettyServer != null && jettyServer.isStarted()) {
            throw new IllegalStateException("server is already started");
        }
        
        jettyServer = new Server(port);
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        handler.setContextPath("/");
        
        handler.addServlet(new ServletHolder(new BeginServlet(database)), "/begin");
        handler.addServlet(new ServletHolder(new GetServlet(database)), "/get");
        handler.addServlet(new ServletHolder(new PutServlet(database)), "/put");
        handler.addServlet(new ServletHolder(new CommitServlet(database)), "/commit");
        handler.addServlet(new ServletHolder(new RollbackServlet(database)), "/rollback");
        handler.addServlet(new ServletHolder(new SizeServlet(database)), "/size");
        
        jettyServer.setHandler(handler);
        jettyServer.start();
        isStarted = false;
    }
    
    public int getPort() {
        return portNum;
    }
    
    public boolean isStarted() {
        return isStarted;
    }
}
