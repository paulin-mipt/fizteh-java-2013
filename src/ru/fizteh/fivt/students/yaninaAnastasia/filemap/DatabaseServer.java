package ru.fizteh.fivt.students.yaninaAnastasia.filemap;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class DatabaseServer {
    private static final int PORT = 8080;
    private Server server = new Server();
    private TransactionWorker transactionWorker;

    public DatabaseServer(TransactionWorker worker) {
        this.transactionWorker = worker;
    }

    public void start(int port) throws Exception {
        if (server != null && server.isStarted()) {
            throw new IllegalStateException("Server is already started");
        }

        if (port == -1) {
            port = PORT;
        }

        server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        context.addServlet(new ServletHolder(new CommandBeginServlet(transactionWorker)), "/begin");
        context.addServlet(new ServletHolder(new CommandGetServlet(transactionWorker)), "/get");
        context.addServlet(new ServletHolder(new CommandPutServlet(transactionWorker)), "/put");
        context.addServlet(new ServletHolder(new CommandCommitServlet(transactionWorker)), "/commit");
        context.addServlet(new ServletHolder(new CommandRollbackServlet(transactionWorker)), "/rollback");
        context.addServlet(new ServletHolder(new CommandSizeServlet(transactionWorker)), "/size");
        server.setHandler(context);
        server.start();
    }

    public int getPortNumber() {
        return server.getConnectors()[0].getPort();
    }

    public void stop() {
        if (server == null || !server.isStarted()) {
            throw new IllegalStateException("Server was not started");
        }
        try {
            server.stop();
        } catch (Exception e) {
            //
        }
        server = null;
    }

    public boolean isStarted() {
        return server.isStarted();
    }
}
