package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.servlet.MyServer;
import ru.fizteh.fivt.students.paulinMatavina.utils.CommandRunner;

public class MultiFileMapMain {
    public static void main(String[] args) {
        final MyServer server;
        MyTableProvider state = null;
        try {
            String property = System.getProperty("fizteh.db.dir");
            state = new MyTableProvider(property);  
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
                    try {
                        server.stop();
                    } catch (Throwable e) {
                        //ignore
                    }
                }
            });
        } catch (Throwable e) {
            try {
                server.stop();
            } catch (Throwable e2) {
                //ignore
            }
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
