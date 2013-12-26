package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;
import ru.fizteh.fivt.students.paulinMatavina.servlet.*;

public class DbServerStart implements Command {
    MyServer server;
    public DbServerStart(MyServer newServer) {
        server = newServer;
    }
    
    @Override
    public int execute(String[] args, State state) {        
        String key = args[0];
        String portString = "10001";
        if (key != null) {
            portString = key;
        }
        
        System.out.println("i'm here, port " + portString);
        if (server.isStarted()) {
            System.out.println("not started: already started");
            return 0;
        }
        
        int port;
        try {
            port = Integer.parseInt(portString);
        } catch (Exception e) {
            System.out.println("not started: wrong port number " + portString);
            return 0;
        }
        try {
            server.start(port);
        } catch (Throwable e) {
            System.out.println("not started: " + e.getMessage());
            return 0;
        }
        
        System.out.println("started at " + port);
        return 0;
    }
    
    @Override
    public String getName() {
        return "starthttp";
    }
    
    @Override
    public int getArgNum() {
        return 0;
    }
    
    @Override
    public boolean spaceAllowed() {
        return true;
    }
}
