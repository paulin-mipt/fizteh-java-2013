package ru.fizteh.fivt.students.dsalnikov.multifilemap;

import ru.fizteh.fivt.students.dsalnikov.shell.Command;
import ru.fizteh.fivt.students.dsalnikov.shell.RmCommand;
import ru.fizteh.fivt.students.dsalnikov.shell.ShellState;

import java.io.File;
import java.io.IOException;

public class DropCommand implements Command {

    @Override
    public void execute(Object state, String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Illegal using of command drop: 1 argument expected");
        } else {
            MultiFileMapState mfhm = (MultiFileMapState) state;
            File deletefile = new File(mfhm.workingdirectory, args[1]);
            if (!deletefile.exists()) {
                System.out.println("table " + args[1] + " not exists");
                return;
            } else {
                if (args[1].equals(deletefile.getName())) {
                    mfhm.notUsingTable();
                }
                RmCommand rm = new RmCommand();
                ShellState ss = new ShellState(deletefile.getAbsolutePath());
                String[] delargs = new String[2];
                delargs[1] = deletefile.getAbsolutePath();
                rm.execute(ss, delargs);
                System.out.println("dropped");
            }
        }
    }

    @Override
    public String getName() {
        return "drop";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
