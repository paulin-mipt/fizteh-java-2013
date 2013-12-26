package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.servlet.MyServer;
import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class DbServerStop implements Command {
    MyServer server;
    public DbServerStop(MyServer newServer) {
        server = newServer;
    }
    
    @Override
    public int execute(String[] args, State state) {
        try {
            if (!server.isStarted()) {
                System.out.println("not started");
                return 0;
            }
            System.out.println("stopped at " + server.getPort());
            server.stop();
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            return 1;
        }
        return 0;
    }
    
    @Override
    public String getName() {
        return "stophttp";
    }
    
    @Override
    public int getArgNum() {
        return 0;
    }
    
    @Override
    public boolean spaceAllowed() {
        return false;
    }
}
