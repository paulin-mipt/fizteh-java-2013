package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.servlet.MyServer;
import ru.fizteh.fivt.students.paulinMatavina.utils.CommandRunner;

public class MultiFileMapMain {
    private static void tryToStop(MyServer server) {
        try {
            server.stop();
        } catch (Throwable e2) {
            //ignore
        }
    }
    
    public static void main(String[] args) {
        final MyServer server;
        MyTableProvider state = null;
        try {
            String property = System.getProperty("fizteh.db.dir");
            state = new MyTableProvider(property);  
        } catch (DbExitException e) {
            System.exit(Integer.parseInt(e.getMessage()));
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        
        server = new MyServer(state);
        try {
            CommandRunner.run(args, state);
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    tryToStop(server);
                }
            });
        } catch (DbExitException e) {
            tryToStop(server);
            System.exit(Integer.parseInt(e.getMessage()));
        }  catch (Throwable e) {
            tryToStop(server);
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
