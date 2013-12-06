package ru.fizteh.fivt.students.abramova.multifilehashmap;

import ru.fizteh.fivt.students.abramova.shell.Command;
import ru.fizteh.fivt.students.abramova.shell.Status;
import java.io.IOException;

public class UseCommand extends Command {
    public UseCommand(String name) {
        super(name);
    }

    @Override
    public int doCommand(String[] args, Status status) throws IOException {
        MultiFileMap multiFile = status.getMultiFileMap();
        if (multiFile == null) {
            throw new IllegalStateException(getName() + ": Command do not get MultiFileMap");
        }
        if (multiFile.setWorkingTable(args[0]) == null) {
            System.out.println(args[0] + " not exists");
        } else {
            System.out.println("using " + args[0]);
        }
        return 0;
    }

    @Override
    public boolean correctArgs(String[] args) {
        return args != null && args.length == 1;
    }
}