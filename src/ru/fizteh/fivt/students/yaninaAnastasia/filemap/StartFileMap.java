package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;
import ru.fizteh.fivt.students.yaninaAnastasia.shell.Shell;

import java.io.IOException;
import java.util.ArrayList;

public class StartFileMap {
    public static void main(String[] args) {
        MultiDBState curState = new MultiDBState();
        Shell shell = new Shell(curState);
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            System.err.println("Error with getting property");
            System.exit(1);
        }
        DatabaseTableProviderFactory factory = new DatabaseTableProviderFactory();
        try {
            curState.database = factory.create(path);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException exp) {
            System.err.println(exp.getMessage());
            System.exit(1);
        }
        ArrayList<Command> cmdList = new ArrayList<Command>();
        cmdList.add(new CommandPut());
        cmdList.add(new CommandGet());
        cmdList.add(new CommandRemove());
        cmdList.add(new CommandCreate());
        cmdList.add(new CommandDrop());
        cmdList.add(new CommandUse());
        cmdList.add(new CommandCommit());
        cmdList.add(new CommandRollback());
        cmdList.add(new CommandSize());
        cmdList.add(new CommandExit());
        shell.fillHashMap(cmdList);
        if (args.length == 0) {
            shell.interActive();
        } else {
            shell.pocket(args);
        }
    }
}

