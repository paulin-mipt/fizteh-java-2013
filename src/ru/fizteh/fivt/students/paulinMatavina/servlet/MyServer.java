package ru.fizteh.fivt.students.paulinMatavina.servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import ru.fizteh.fivt.students.paulinMatavina.filemap.MyTableProvider;

public class MyServer {
    private Server jettyServer;
    private Database database;
    private int portNum;
    public MyServer(MyTableProvider provider) {
        database = new Database(provider);
    }
    
    public void stop() throws IllegalStateException {
        try {
            jettyServer.stop();
        } catch (Throwable e) {
            //do nothing
        } finally {
            jettyServer = null;
        }
    }

    public void start(int port) throws Exception {
        portNum = port;
        jettyServer = new Server(port);
        
        try {
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
        } catch (Throwable e) {
            jettyServer = null;
            throw e;
        }
    }
    
    public int getPort() {
        return portNum;
    }
    
    public boolean isStarted() {
        return jettyServer != null;
    }
}
